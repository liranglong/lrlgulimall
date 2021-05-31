package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.EsConstant;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GulimallElasticSearch;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    RestHighLevelClient client;
    @Autowired
    ProductFeignService productFeignService;

    /**
     * 在es检索
     *
     * @param param 检索的所有参数
     * @return 返回的结果
     */
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        // 1、动态构建检索需要的DSL语句

        // 1、准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // 2、执行检索请求
            SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearch.COMMON_OPTIONS);

            // 3、分析响应数据，封装成需要的格式
            result = buildSearchResponse(param, searchResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 准备检所请求
     * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮 ,聚合分析【分析所有可选的规格、分类、品牌】
     * <p>
     * 1、创建查询请求
     * SearchRequest searchRequest = new SearchRequest("newbank")
     * 2、创建构建DSL检索条件对象
     * SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
     * 3、创建各种条件
     * QueryBuilder boolQuery = QueryBuilders.boolQuery()
     * QueryBuilder matchQuery = QueryBuilders.matchAllQuery()
     * <p>
     * 4、组合检索条件
     * sourceBuilder.sort();
     * sourceBuilder.from();
     * sourceBuilder.size();
     * sourceBuilder.aggregation();
     * sourceBuilder.query(QueryBuilder);
     * sourceBuilder.query(boolQuery);
     * 5、请求绑定条件
     * searchRequest.source(sourceBuilder);
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // 构建DSL语句对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        // 1、构建bool - query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 1.1、must【得分】
        // 分词匹配skuTitle
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        // 1.2、filter【无得分】
        // 三级分类
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }

        // 1.3、品牌
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }

        // 1.4、属性
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                // must中的必须是同时满足的，如果boolQuery不是内层的
                // 那么boolQuery会拼接多个must(attrs.attrId)，例如id = 6时，同时必须id = 7，没有此attr
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                // 每一个属性都要生成一个嵌入式查询
                // 商品必须包含每一个 传入的属性
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            }
        }

        // 1.5、库存
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }

        // 1.6、价格区间 0_500  _500  500_
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }

        // 1、END 封装查询条件
        sourceBuilder.query(boolQueryBuilder);

        /**
         * 排序，分页，高亮
         */
        // 2.1、排序
        if (!StringUtils.isEmpty(param.getSort())) {
            String[] s = param.getSort().split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }

        // 2.2、分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3、高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析【品牌、分类、分析所有可选的规格】
         */
        // 3.1、品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(10);
        // 子聚合，获得品牌name和图片
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        // 构建DSL
        // TODO 聚合品牌
        sourceBuilder.aggregation(brand_agg);

        // 3.2、分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        // 子聚合，获得分类名
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        // 构建DSL
        // TODO 聚合分类
        sourceBuilder.aggregation(catalog_agg);

        // 3.3、规格聚合【嵌入式】
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 根据id分组
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        // 子聚合=》在id分组内部，继续按照 attr_name分组
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 子聚合=》在id分组内部，继续按照 attr_value分组
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        // 嵌入式聚合
        attr_agg.subAggregation(attr_id_agg);
        // 构建DSL
        // TODO 聚合属性规格
        sourceBuilder.aggregation(attr_agg);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

        // 打印DSL
        System.out.println(sourceBuilder.toString());
        return searchRequest;
    }

    /**
     * 封装检索结果
     * 1、返回所有查询到的商品
     * 2、当前所有商品涉及到的所有属性信息
     * 3、当前所有商品涉及到的所有品牌信息
     * 4、当前所有商品涉及到的所有分类信息
     * 5、分页信息   pageNum:当前页码  total:总记录数    totalPages: 总页码
     */
    private SearchResult buildSearchResponse(SearchParam param, SearchResponse searchResponse) {
        SearchResult result = new SearchResult();
        SearchHits hits = searchResponse.getHits();
        // 1、返回所有查询到的商品
        List<SkuEsModel> products = new ArrayList<>();
        if (!ArrayUtils.isEmpty(hits.getHits())) {
            for (SearchHit hit : hits.getHits()) {
                String jsonStr = hit.getSourceAsString();
                SkuEsModel model = JSON.parseObject(jsonStr, SkuEsModel.class);
                // 设置高亮信息
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    model.setSkuTitle(skuTitle.getFragments()[0].string());
                }
                products.add(model);
            }
        }
        result.setProducts(products);

        // 2、当前所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> attrBuckets = attr_id_agg.getBuckets();
        for (Terms.Bucket bucket : attrBuckets) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 提取属性ID
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 提取属性名字
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
            // 提取品牌图片
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attr_value_agg.getBuckets().stream().map(item -> {
                return ((Terms.Bucket) item).getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);


        // 3、当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        for (Terms.Bucket bucket : brandBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 提取品牌ID
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            // 提取品牌名字
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
            // 提取品牌图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 4、当前所有商品涉及到的所有分类信息
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> catalogBuckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : catalogBuckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            // 提取分类Id
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            // 提取分类名字
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            catalogVo.setCatalogName(catalog_name_agg.getBuckets().get(0).getKeyAsString());
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        // 5、分页信息   pageNum:当前页码 、total:总记录数 、totalPages: 总页码
        long total = hits.getTotalHits().value;
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : ((int) total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setPageNum(param.getPageNum());
        result.setTotal(total);
        result.setTotalPages(totalPages);

        // 6、所有页码数
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 7、构建面包屑导航功能
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                //1、分析每一个attrs传过来的参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                // attrs=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                // 根据请求构造面包屑 规格属性Id集合，这个集合包含的属性规格不显示【前端会遍历每个参数显示】
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    // 设置属性名
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                //2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                //拿到所有的查询条件，去掉当前
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(collect);
        }

        // 品牌、分类
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            // TODO 远程查询所有品牌
            R r = productFeignService.brandsInfo(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer sb = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    sb.append(brandVo.getName()+";");
                    replace = replaceQueryString(param, brandVo.getBrandId()+"", "brandId");
                }
                navVo.setNavValue(sb.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        // TODO 分类 面包屑


        return result;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");  //浏览器对空格的编码和Java不一样，差异化处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 就是点了X之后，应该跳转的地址
        // 这里要判断一下，attrs是不是第一个参数，因为第一个参数 没有&符号
        // TODO BUG，第一个参数不带&
        return param.get_queryString().replace("&"+ key + "=" + encode, "");
    }

}
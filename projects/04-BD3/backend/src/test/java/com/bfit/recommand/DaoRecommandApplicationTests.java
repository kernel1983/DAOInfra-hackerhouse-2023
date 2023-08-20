package com.bfit.recommand;

import com.bfit.recommand.common.TokenizerEnum;
import com.bfit.recommand.data.entity.ProjectInfo;
import com.bfit.recommand.data.entity.UserInfo;
import com.bfit.recommand.data.entity.UserProject;
import com.bfit.recommand.repo.ProjectInfoRepository;
import com.bfit.recommand.repo.UserInfoRepository;
import com.bfit.recommand.repo.UserProjectRepository;
import com.bfit.recommand.service.ContentBasedRecommendationUtil;
import com.bfit.recommand.web.dto.RecommandNeed;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class DaoRecommandApplicationTests {


    ObjectMapper mapper;
    {
        mapper = new ObjectMapper();
    }
    String[] arr = new String[]{"The partner's expertise was evident throughout the project. They brought a depth of knowledge that was invaluable. However, during critical phases, their responsiveness left something to be desired. It would be beneficial if they could maintain consistent communication, especially during crunch times.",

            "Initially, their team members were proactive, taking initiative and driving tasks forward. As the project progressed, there seemed to be a noticeable drop in momentum. It's crucial for the team to maintain energy and focus throughout the entire project lifecycle.",

            "They provided valuable insights at the project's inception, showcasing their experience. But as we moved into execution, there was a noticeable lack of vigor. The transition from planning to action needs to be more seamless.",

            "Their ability to handle unexpected challenges was commendable. They showcased adaptability and resilience. However, when it came to routine communication and updates, there were gaps. Regular check-ins are as vital as crisis management.",

            "Their attention to detail was impressive. They caught nuances that could have easily been overlooked. However, there were moments when they seemed to miss the broader perspective. Balancing micro and macro views is essential.",

            "On a technical front, they were stellar. Their skills and knowledge were top-notch. But the project could have benefited from stronger management and coordination. Technical prowess needs to be complemented with effective project management.",

            "Their passion and commitment to the project were palpable. They were invested in the project's success. However, their resource allocation strategies were conservative, which sometimes hindered progress. Being more agile with resources can drive better results.",

            "The partner displayed great team spirit and collaboration. They integrated well with our internal teams. However, their internal decision-making processes were sometimes slow, causing delays. Streamlining decisions can greatly improve project pace.",

            "Their innovative approach was evident in many project aspects. They brought fresh ideas and solutions. However, when it came to practical implementation, they were occasionally too cautious. Innovation should be paired with bold action.",

            "Their experience and industry knowledge added immense value. They brought insights that we hadn't considered. However, their feedback loops and revision processes needed to be more timely. Swift iterations can lead to more efficient outcomes."};

    private List<String> remarkList = List.of(arr);

    @Resource
    private UserProjectRepository userProjectRepository;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private ProjectInfoRepository projectInfoRepository;


    String prjAddr = "prj_655656ae65f66c0987ba0000787c";
    @Test
    public void contextLoads() {

        List<UserProject> upList = new ArrayList<>(remarkList.size());
        Date date = new Date();
        String issAddr = "0x655656ae65f66c0987ba8799787c";

        remarkList.forEach(x->{
            UserProject userProject = UserProject.builder()
                    .projectAddress(prjAddr).issuerAddress(issAddr)
                    .reviewerAddress("0x" + StringUtils.replace(UUID.randomUUID().toString(), "-", ""))
                    .reviewScore(0)
                    .remark(x)
                    .dbCreateTime(date).dbUpdateTime(date).build();
            upList.add(userProject);
        });

        userProjectRepository.insertBatch(upList);
    }

    @SneakyThrows
    @Test
    public void distributeTest(){

        List<ProjectInfo> projectInfos = fuzzyQuery("Nostra contact");
        List<String> projectAddressList = projectInfos.stream().map(ProjectInfo::getProjectAddress).collect(Collectors.toList());
        List<UserProject> userProjects = userProjectRepository.queryListByProjectAddressList(projectAddressList);

        Map<Long, String> src = userProjects.stream().collect(Collectors.toMap(UserProject::getId, UserProject::getRemark));
        src.put(TokenizerEnum.GOOD.getCode(), TokenizerEnum.GOOD.getDesc());
        ContentBasedRecommendationUtil cb = new ContentBasedRecommendationUtil(src);
        List<Long> recommendations = cb.getRecommendations(TokenizerEnum.GOOD.getCode());

        List<UserInfo> userInfos = userInfoRepository.queryByUserWalletList(new ArrayList<>(userProjects.stream().map(UserProject::getReviewerAddress).collect(Collectors.toSet())));
        List<RecommandNeed> resultList = new ArrayList<>();
        recommendations.forEach(x->{
            UserProject userProject = userProjects.stream().filter(e -> x.equals(e.getId())).findFirst().orElse(null);
            if (Objects.nonNull(userProject)){
                UserInfo userInfo = userInfos.stream().filter(e -> userProject.getReviewerAddress().equals(e.getUserWallet())).findFirst().orElse(null);
                if (Objects.nonNull(userInfo)){
                    RecommandNeed recommandNeed = RecommandNeed.builder().name(userInfo.getUserName()).avatar(userInfo.getAvatar()).walletAddress(userInfo.getUserWallet()).build();
                    resultList.add(recommandNeed);
                }
            }
        });

        log.info("OUTPUT :: {}", mapper.writeValueAsString(resultList));
    }


    @Test
    public void initUserInfoTest(){
        List<UserProject> userProjects = userProjectRepository.selectAll();
        Date date = new Date();

        List<UserInfo> list = new ArrayList<>();
        userProjects.forEach(x->{
            UserInfo build = UserInfo.builder()
                    .avatar("https://source.unsplash.com/512x512/?gaming")
                    .userWallet(x.getReviewerAddress())
                    .userName(new Random().nextInt() * 12 + "DAO")
                    .deleted(false).dbCreateTime(date).dbUpdateTime(date).build();
            list.add(build);
        });
        userInfoRepository.insertBatch(list);
    }




    @Resource
    private RestHighLevelClient restHighLevelClient;

    @SneakyThrows
    @Test
    void testCreateIndex() {
        //1.创建索引请求
//        CreateIndexRequest request = new CreateIndexRequest("user_info");
        CreateIndexRequest request = new CreateIndexRequest("project_info");
//        CreateIndexRequest request = new CreateIndexRequest("user_project");
        //2.客户端执行请求IndicesClient，执行create方法创建索引，请求后获得响应
        CreateIndexResponse response=
                restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    void testExistIndex() {
        //1.查询索引请求
        GetIndexRequest request=new GetIndexRequest("user_info");
        //2.执行exists方法判断是否存在
        boolean exists=restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @SneakyThrows
    @Test
    void testDeleteIndex() {
        //1.删除索引请求
        DeleteIndexRequest request=new DeleteIndexRequest("project_info");
        //执行delete方法删除指定索引
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    @Test
    public void queryProjectInfoTest(){
        System.out.println(userInfoRepository.queryAll());
        System.out.println(projectInfoRepository.queryByProjectAddress("prj_655656ae65f66c0987ba0000787a"));
    }


    @SneakyThrows
    @Test
    public void testAddUser() {
        //1.创建对象
        List<UserInfo> userInfoList = userInfoRepository.queryAll();
//        List<ProjectInfo> projectInfos = projectInfoRepository.queryAll();
        //2.创建请求
        IndexRequest request=new IndexRequest("user_info");
//        IndexRequest request=new IndexRequest("project_info");
        //3.设置规则 PUT /ljx666/_doc/1
        //设置文档id=6，设置超时=1s等，不设置会使用默认的
        //同时支持链式编程如 request.id("6").timeout("1s");
        request.timeout("1s");

        //4.将数据放入请求，要将对象转化为json格式
        //XContentType.JSON，告诉它传的数据是JSON类型
        userInfoList.forEach(x->request.source(JSONValue.toJSONString(x), XContentType.JSON));
//        projectInfos.forEach(x->request.source(JSONValue.toJSONString(x), XContentType.JSON));
//        request.source(JSONValue.toJSONString(user), XContentType.JSON);

        //5.客户端发送请求，获取响应结果
        IndexResponse indexResponse=restHighLevelClient.index(request,RequestOptions.DEFAULT);
    }

    @Test
    void testBulkAddUser() throws IOException {
        BulkRequest bulkRequest=new BulkRequest();
        //设置超时
        bulkRequest.timeout("10s");

        List<ProjectInfo> projectInfos = projectInfoRepository.queryAll();

        //批量处理请求
        for (ProjectInfo u :projectInfos){
            //不设置id会生成随机id
            bulkRequest.add(new IndexRequest("project_info")
                    .id(String.valueOf(u.getId()))
                    .source(mapper.writeValueAsBytes(u),XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());//是否执行失败,false为执行成功
    }


    @SneakyThrows
    private List<ProjectInfo> fuzzyQuery(String item){
        SearchRequest searchRequest=new SearchRequest("project_info");//里面可以放多个索引
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();//构造搜索条件

        //此处可以使用QueryBuilders工具类中的方法
        //1.查询所有
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //2.查询name中含有Java的
        sourceBuilder.query(QueryBuilders.multiMatchQuery(item,"description"));
        //3.分页查询
        sourceBuilder.from(0).size(5);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse=restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //获取总条数
        System.out.println(searchResponse.getHits().getTotalHits().value);
        //输出结果数据（如果不设置返回条数，大于10条默认只返回10条）
        SearchHit[] hits=searchResponse.getHits().getHits();
        //输出结果数据（如果不设置返回条数，大于10条默认只返回10条）
        return Arrays.stream(hits).map(x ->
             mapper.convertValue(x.getSourceAsMap(), ProjectInfo.class)).collect(Collectors.toList());
    }

    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest=new SearchRequest("project_info");//里面可以放多个索引
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();//构造搜索条件

        //此处可以使用QueryBuilders工具类中的方法
        //1.查询所有
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //2.查询name中含有Java的
        sourceBuilder.query(QueryBuilders.multiMatchQuery("DID","description"));
        //3.分页查询
        sourceBuilder.from(0).size(5);

        //4.按照score正序排列
        //sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.ASC));
        //5.按照id倒序排列（score会失效返回NaN）
        //sourceBuilder.sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC));

        //6.给指定字段加上指定高亮样式
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("description").preTags("<span style='color:red;'>").postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse=restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //获取总条数
        System.out.println(searchResponse.getHits().getTotalHits().value);
        //输出结果数据（如果不设置返回条数，大于10条默认只返回10条）
        SearchHit[] hits=searchResponse.getHits().getHits();
        for(SearchHit hit :hits){
            System.out.println("分数:"+hit.getScore());
            Map<String,Object> source=hit.getSourceAsMap();
            System.out.println("index->"+hit.getIndex());
            System.out.println("id->"+hit.getId());
            ProjectInfo projectInfo = mapper.convertValue(source, ProjectInfo.class);
            log.info("projectInfo, {}", projectInfo);
//            for(Map.Entry<String,Object> s:source.entrySet()){
//                System.out.println(s.getKey()+"--"+s.getValue());
//            }
        }
    }

}

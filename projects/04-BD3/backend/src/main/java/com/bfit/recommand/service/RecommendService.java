package com.bfit.recommand.service;

import com.bfit.recommand.common.TokenizerEnum;
import com.bfit.recommand.data.entity.ProjectInfo;
import com.bfit.recommand.data.entity.UserInfo;
import com.bfit.recommand.data.entity.UserProject;
import com.bfit.recommand.repo.ProjectInfoRepository;
import com.bfit.recommand.repo.UserInfoRepository;
import com.bfit.recommand.repo.UserProjectRepository;
import com.bfit.recommand.web.dto.RecommandNeed;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestHighLevelClient restHighLevelClient;
    private final UserProjectRepository userProjectRepository;
    private final UserInfoRepository userInfoRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<RecommandNeed> queryRecommend(String walletAddress, String item){

        List<ProjectInfo> projectInfos = fuzzyQuery(item);
        if (null == projectInfos || projectInfos.isEmpty()){
            projectInfos = projectInfoRepository.queryRecentByIssuer(walletAddress);
        }

        if (null == projectInfos || projectInfos.isEmpty()){
            return Collections.EMPTY_LIST;
        }

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

        return resultList;
    }


    @SneakyThrows
    private List<ProjectInfo> fuzzyQuery(String item){
        SearchRequest searchRequest = new SearchRequest("project_info");//里面可以放多个索引
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构造搜索条件

        //此处可以使用QueryBuilders工具类中的方法
        //1.查询所有
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //2.查询name中含有Java的
        sourceBuilder.query(QueryBuilders.multiMatchQuery(item,"description"));
        //3.分页查询
        sourceBuilder.from(0).size(15);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //获取总条数
        System.out.println(searchResponse.getHits().getTotalHits().value);
        //输出结果数据（如果不设置返回条数，大于10条默认只返回10条）
        SearchHit[] hits=searchResponse.getHits().getHits();
        //输出结果数据（如果不设置返回条数，大于10条默认只返回10条）
        return Arrays.stream(hits).map(x ->
                mapper.convertValue(x.getSourceAsMap(), ProjectInfo.class)).collect(Collectors.toList());
    }

}

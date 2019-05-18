package build.dream.auth.services;

import build.dream.auth.constants.Constants;
import build.dream.common.saas.domains.OauthClientDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OauthClientDetailService {
    @Transactional(readOnly = true)
    public OauthClientDetail obtainOauthClientDetail(String clientId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(OauthClientDetail.ColumnName.CLIENT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, clientId);
        return DatabaseHelper.find(OauthClientDetail.class, searchModel);
    }
}

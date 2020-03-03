package build.dream.auth.services;

import build.dream.auth.constants.Constants;
import build.dream.common.domains.saas.Tenant;
import build.dream.common.domains.saas.TenantSecretKey;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {
    @Transactional(readOnly = true)
    public Tenant obtainTenant(Long tenantId) {
        return DatabaseHelper.find(Tenant.class, tenantId);
    }

    @Transactional(readOnly = true)
    public TenantSecretKey obtainTenantSecretKey(Long tenantId) {
        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(TenantSecretKey.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .build();
        return DatabaseHelper.find(TenantSecretKey.class, searchModel);
    }
}

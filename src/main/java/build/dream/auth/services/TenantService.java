package build.dream.auth.services;

import build.dream.auth.constants.Constants;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.saas.domains.TenantSecretKey;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class TenantService {
    @Transactional(readOnly = true)
    public Tenant obtainTenant(BigInteger tenantId) {
        return DatabaseHelper.find(Tenant.class, tenantId);
    }

    @Transactional(readOnly = true)
    public TenantSecretKey obtainTenantSecretKey(BigInteger tenantId) {
        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(TenantSecretKey.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .build();
        return DatabaseHelper.find(TenantSecretKey.class, searchModel);
    }
}

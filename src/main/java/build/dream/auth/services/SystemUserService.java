package build.dream.auth.services;

import build.dream.auth.mappers.SystemUserMapper;
import build.dream.common.domains.saas.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemUserService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Transactional(readOnly = true)
    public SystemUser findByLoginNameOrEmailOrMobile(String loginName) {
        return systemUserMapper.findByLoginNameOrEmailOrMobile(loginName);
    }
}

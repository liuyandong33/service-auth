package build.dream.auth.services;

import build.dream.auth.mappers.PrivilegeMapper;
import build.dream.common.domains.saas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrivilegeService {
    @Autowired
    private PrivilegeMapper privilegeMapper;

    /**
     * 获取用户APP权限
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<AppPrivilege> obtainUserAppPrivileges(Long userId) {
        return privilegeMapper.obtainUserAppPrivileges(userId);
    }

    /**
     * 获取用户POS权限
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<PosPrivilege> obtainUserPosPrivileges(Long userId) {
        return privilegeMapper.obtainUserPosPrivileges(userId);
    }

    /**
     * 获取用户后台权限
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<BackgroundPrivilege> obtainUserBackgroundPrivileges(Long userId) {
        return privilegeMapper.obtainUserBackgroundPrivileges(userId);
    }

    /**
     * 获取用户运营平台权限
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<OpPrivilege> obtainUserOpPrivileges(Long userId) {
        return privilegeMapper.obtainUserOpPrivileges(userId);
    }

    /**
     * 获取用户运维平台权限
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<DevOpsPrivilege> obtainUserDevOpsPrivileges(Long userId) {
        return privilegeMapper.obtainUserDevOpsPrivileges(userId);
    }
}

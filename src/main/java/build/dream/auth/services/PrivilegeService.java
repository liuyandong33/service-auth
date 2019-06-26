package build.dream.auth.services;

import build.dream.auth.mappers.PrivilegeMapper;
import build.dream.common.saas.domains.PosPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class PrivilegeService {
    @Autowired
    private PrivilegeMapper privilegeMapper;

    @Transactional(readOnly = true)
    public List<PosPrivilege> obtainUserPosPrivileges(BigInteger userId) {
        return privilegeMapper.obtainUserPosPrivileges(userId);
    }
}

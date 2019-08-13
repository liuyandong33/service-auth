package build.dream.auth.services;

import build.dream.common.domains.saas.Agent;
import build.dream.common.utils.DatabaseHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class AgentService {
    @Transactional(readOnly = true)
    public Agent obtainAgent(BigInteger agentId) {
        return DatabaseHelper.find(Agent.class, agentId);
    }
}

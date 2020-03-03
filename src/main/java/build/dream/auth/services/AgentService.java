package build.dream.auth.services;

import build.dream.common.domains.saas.Agent;
import build.dream.common.utils.DatabaseHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentService {
    @Transactional(readOnly = true)
    public Agent obtainAgent(Long agentId) {
        return DatabaseHelper.find(Agent.class, agentId);
    }
}

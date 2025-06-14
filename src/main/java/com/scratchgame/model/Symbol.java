package com.scratchgame.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Symbol {

    @JsonProperty("reward_multiplier")
    private BigDecimal rewardMultiplier;

    private String type;
    private String impact;
    private BigDecimal extra;
    public BigDecimal getRewardMultiplier() { return rewardMultiplier; }
    public void setRewardMultiplier(BigDecimal rewardMultiplier) { this.rewardMultiplier = rewardMultiplier; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }

    public BigDecimal getExtra() { return extra; }
    public void setExtra(BigDecimal extra) { this.extra = extra; }

    public boolean isStandard() { return "standard".equals(type); }
    public boolean isBonus() { return "bonus".equals(type); }
}

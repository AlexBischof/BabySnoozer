package babysnoozer.handlers.commands;

public class LearnCycle {

    private long learnValue;
    private final long displayValueMultiplicator;
    private final long rotiValueMultiplicator;

    public LearnCycle(long rotiValueMultiplicator, long displayValueMultiplicator)
    {
        this.displayValueMultiplicator = displayValueMultiplicator;
        this.rotiValueMultiplicator = rotiValueMultiplicator;
    }

    public String getDisplayValue()
    {
        return String.valueOf(this.learnValue * displayValueMultiplicator);
    }

    public Long getLearnValue()
    {
        return learnValue;
    }

    public void setLearnValue(long rotiValue)
    {
        this.learnValue = rotiValue * rotiValueMultiplicator;
    }

    public void resetLearnValue()
    {
        learnValue = 0;
    }

}

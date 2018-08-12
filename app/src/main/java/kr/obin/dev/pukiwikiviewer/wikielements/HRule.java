package kr.obin.dev.pukiwikiviewer.wikielements;

public class HRule extends WikiElement
{
    public HRule()
    {
        super();
    }

    public boolean canContain(WikiElement e)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "hr"; // FIXME
    }
}

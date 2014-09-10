package fr.skyforce77.irckeeper.commands;

import java.util.ArrayList;

public class Command {

    public String label;
    public boolean isalias;
    public String real;
    private Argument[] arguments;
    private ArrayList<String> alias = new ArrayList<String>();

    public void onTyped(String[] args) {
    }

    public void onInitialized(String label) {
    }

    public void setArguments(Argument... args) {
        arguments = args;
    }

    public String getUse() {
        String ali = null;
        if (alias != null && alias.size() > 0) {
            ali = "Alias: ";
            for (String alia : alias) {
                ali = ali + "," + alia;
            }
            ali = ali.replaceFirst(",", "");
        }
        if(ali != null)
        	System.out.println(ali);

        String s = label;
        if (arguments != null) {
            for (Argument argu : arguments) {
                s = s+" "+argu.getRender();
            }
        }
        return s;
    }

    public boolean hasAlias(String alias) {
        return this.alias.contains(alias);
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void setAlias(String... args) {
        for (String s : args) {
            alias.add(s);
        }
    }

    public boolean isCorrect(String[] args) {
        return true;
    }

    protected boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

package me.phoenixra.russian_roulette.files;

import me.phoenixra.core.files.PhoenixFile;
import me.phoenixra.core.files.PhoenixFileManager;

public class LangFile extends PhoenixFile {

    public LangFile(PhoenixFileManager fileM) {
        super(fileM, "lang", new LangClass());
    }

    @Override
    public boolean handleLoad() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean reloadAction() {
        // TODO Auto-generated method stub
        return true;
    }
}

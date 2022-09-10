package me.phoenixra.core.files;

import me.phoenixra.core.PhoenixUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PhoenixFile {
    private final String fileName;
    private File file;
    private FileConfiguration fileConf;
    private final PhoenixFileManager fileM;
    private final PhoenixFileClass fileClass;
    public PhoenixFile(PhoenixFileManager fileM, String name, PhoenixFileClass fileClass){
        this.fileM=fileM;
        fileName=name;
        this.fileClass=fileClass;
    }
    public abstract boolean handleLoad();
    public abstract boolean reloadAction();

    @SuppressWarnings("unchecked")
    public boolean load() {
        file = new File(fileM.getPlugin().getDataFolder(), fileName+".yml");
        if(file.exists()) {
            return reload();
        }
        if(!fileM.getPlugin().getDataFolder().exists()) fileM.getPlugin().getDataFolder().mkdir();
        if(fileClass!=null) {
            fileConf = fileM.loadFromResource(fileName+".yml", file, false);
            PrintWriter pw;
            try {
                pw = new PrintWriter(this.getFile());
                int i = -1;

                for(Field f : fileClass.getClass().getFields()) {
                    i++;
                    PhoenixFileClass.ConfigHeader head = f.getAnnotation(PhoenixFileClass.ConfigHeader.class);
                    PhoenixFileClass.ConfigKey key = f.getAnnotation(PhoenixFileClass.ConfigKey.class);

                    if(key==null) {
                        continue;
                    }
                    if(head==null) {
                        continue;
                    }
                    String path=key.path();
                    if(path.contains(".")&&path.split("\\.").length>0) {
                        path=path.split("\\.")[path.split("\\.").length-1];

                    }

                    if(head.value()!=null) {
                        for(String s : head.value()) {
                            if(!s.equals("")) {
                                pw.println(key.space()+s);
                            }
                        }
                    }
                    if(key.isSection()) {
                        pw.println(key.space() + path + ":");
                        continue;
                    }
                    if(f.getType()==int.class) {
                        pw.println(key.space()+path+": " + f.getInt(fileClass.getReference(i)));
                        continue;
                    }
                    if(f.getType()==double.class) {
                        pw.println(key.space()+path+": " + f.getDouble(fileClass.getReference(i)));
                        continue;
                    }
                    if(f.getType()==long.class) {
                        pw.println(key.space()+path+": " + f.getLong(fileClass.getReference(i)));
                        continue;
                    }
                    if(f.getType()==String.class) {
                        pw.println(key.space()+path+": " + "\"" + f.get(fileClass.getReference(i)) + "\"");
                        f.set(fileClass.getReference(i), PhoenixUtils.colorFormat(((String)f.get(fileClass.getReference(i)))).replace("\\n","\n"));
                        continue;
                    }
                    if(f.getType()==boolean.class) {
                        pw.println(key.space()+path+": " + f.get(fileClass.getReference(i)));
                        continue;
                    }
                    if(f.getType()== List.class) {
                        List<String> var = (List<String>) f.get(fileClass.getReference(i));
                        List<String> list=new ArrayList<>();
                        pw.println(key.space()+path + ":");
                        for(String s : var){
                            pw.println(key.space()+"- \""+s+"\"");
                            list.add(PhoenixUtils.colorFormat(s).replace("\\n","\n"));
                        }
                        f.set(fileClass.getReference(i), list);
                    }
                }
                pw.close();
            } catch (FileNotFoundException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }else {
            fileConf = fileM.loadFromResource(fileName+".yml", file, true);
        }
        try {
            if(!handleLoad()) {
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public boolean reload() {
        fileConf= YamlConfiguration.loadConfiguration(file);
        if(fileClass!=null) {
            try {
                int i = -1;
                for(Field f : fileClass.getClass().getFields()) {
                    i++;
                    PhoenixFileClass.ConfigKey key = f.getAnnotation(PhoenixFileClass.ConfigKey.class);
                    if(key.isSection()) {
                        continue;
                    }
                    if(this.getFileC().getString(key.path())!=null) {
                        try {
                            if(f.getType()==boolean.class) {
                                f.set(fileClass.getReference(i), this.getFileC().getBoolean(key.path()));
                                continue;
                            }
                            if(f.getType()==int.class) {
                                f.set(fileClass.getReference(i), this.getFileC().getInt(key.path()));
                                continue;
                            }
                            if(f.getType()==long.class) {
                                f.set(fileClass.getReference(i), this.getFileC().getLong(key.path()));
                                continue;
                            }
                            if(f.getType()==double.class) {
                                f.set(fileClass.getReference(i), this.getFileC().getDouble(key.path()));
                                continue;
                            }
                            if(f.getType()==String.class) {
                                f.set(fileClass.getReference(i), PhoenixUtils.colorFormat(this.getFileC().getString(key.path())).replace("\\n","\n"));
                                continue;
                            }
                            if(f.getType()==List.class) {
                                List<String> l= new ArrayList<>();
                                for(String s:this.getFileC().getStringList(key.path())) {

                                    l.add(PhoenixUtils.colorFormat(s).replace("\\n","\n"));
                                }
                                f.set(fileClass.getReference(i), l);

                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return reloadAction();
    }
    public boolean save() {
        try {
            fileConf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public FileConfiguration getFileC() {
        return fileConf;
    }

    @SuppressWarnings("unused")
    private FileConfiguration loadFromResource(String name, File out, boolean copy) {
        try {
            InputStream is =  fileM.getPlugin().getResource(name);
            FileConfiguration f = YamlConfiguration.loadConfiguration(out);
            if(is!=null) {
                InputStreamReader isReader = new InputStreamReader(is);
                f.setDefaults(YamlConfiguration.loadConfiguration(isReader));
                f.options().copyDefaults(copy);
                f.save(out);
            }
            return f;
        }catch(IOException e) {
            return null;
        }
    }
    public String getFileName() {
        return fileName;
    }
    public File getFile() {
        return file;
    }

}

package com.github.mnicky.bible4j.storage;

/**
 * Interface of factory providing the BibleStorage.
 */
public interface BibleStorageFactory {

    public BibleStorage createBibleStorage();
    
    public void setUrl(String url);

    public void setUser(String user);

    public void setPassword(String password);
}

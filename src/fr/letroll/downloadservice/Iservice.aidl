package fr.letroll.downloadservice;

interface Iservice{
    int getCounter();
    void addDownload( in String aString);
    void addDownloads( in List<String> list);
    void setDestinationPath( in String aPath);
    void setPos(in int aPos);
}

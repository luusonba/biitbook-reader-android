package lmquan1990.biitbook;

public class Dowing{
    //private variables
	int _stt;
    String _userid;
    String _idbook;
    String _url;
    String _name;
    String _author;
    String _dowing;
    
    // Empty constructor
    public Dowing(){

    }
    // constructor
    public Dowing(int stt, String userid, String idbook, String url, String name, String author, String dowing){
    	this._stt = stt;
    	this._userid = userid;
    	this._idbook = idbook;
    	this._url = url;
        this._name = name;
        this._author = author;
        this._dowing = dowing;
    }


    //stt
    public int getStt(){
        return this._stt;
    }

    public void setStt(int stt){
        this._stt = stt;
    }

    //userid
    public String getUserid(){
        return this._userid;
    }

    public void setUserid(String userid){
        this._userid = userid;
    }
    
    //idbook
    public String getIdbook(){
        return this._idbook;
    }

    public void setIdbook(String idbook){
        this._idbook = idbook;
    }
    
  //url
    public String getUrl(){
        return this._url;
    }
    
    public void setUrl(String url){
        this._url = url;
    }

    // name
    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }
    
  //author
    public String getAuthor(){
        return this._author;
    }
    
    public void setAuthor(String author){
        this._author = author;
    }
    
  //dowing
    public String getDowing(){
        return this._dowing;
    }
    
    public void setDowing(String downing){
        this._dowing = downing;
    }
}
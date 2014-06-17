package lmquan1990.biitbook;

public class EBook{
    //private variables
	int _stt;
    String _id;
    String _name;
    String _author;
    String _image_view;
    String _url;
    String _point;    
    String _cat;
    String _book_path;
    String _type;
    String _onoff;
    String _pass;
    String _userid;

    // Empty constructor
    public EBook(){

    }
    // constructor
    public EBook(int stt, String id, String name, String author, String image_view,
    		String url, String point, String cat, String book_path, String type, 
    		String onoff, String pass, String userid){
    	this._stt = stt;
    	this._id = id;
        this._name = name;
        this._author = author;
        this._image_view = image_view;        
        this._point = point;
        this._url = url;
        this._cat = cat;
        this._book_path = book_path;
        this._type = type;
        this._onoff = onoff;
        this._pass = pass;
        this._userid = userid;
    }

    /** // constructor
    public EBook(String name, String _phone_number){
        this._name = name;
        this._phone_number = _phone_number;
    }**/
    //stt
    public int getStt(){
        return this._stt;
    }

    public void setStt(int stt){
        this._stt = stt;
    }

    //id
    public String getID(){
        return this._id;
    }

    public void setID(String id){
        this._id = id;
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
    
    //image_view
    public String getImage_view(){
        return this._image_view;
    }

    public void setImage_view(String image_view){
        this._image_view = image_view;
    }
    
    //url
    public String getUrl(){
        return this._url;
    }
    
    public void setUrl(String url){
        this._url = url;
    }
   
    //point
    public String getPoint(){
        return this._point;
    }

    public void setPoint(String point){
        this._point = point;
    }    
      
    //cat
    public String getCat(){
        return this._cat;
    }

    public void setCat(String cat){
        this._cat = cat;
    }
    
    //book_path
    public String getBook_path(){
        return this._book_path;
    }

    public void setBook_path(String book_path){
        this._book_path = book_path;
    }    
   
    //type
    public String getType(){
        return this._type;
    }

    public void setType(String type){
        this._type = type;
    }
    
    //onoff
    public String getOnoff(){
        return this._onoff;
    }

    public void setOnoff(String onoff){
        this._onoff = onoff;
    }
    
    //pass
    public String getPass(){
        return this._pass;
    }

    public void setPass(String pass){
        this._pass = pass;
    }
    
  //userid
    public String getUserid(){
        return this._userid;
    }

    public void setUserid(String userid){
        this._userid = userid;
    }
}
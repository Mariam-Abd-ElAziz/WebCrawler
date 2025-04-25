package invertedIndex;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ehab
 */
public class SourceRecord {
    public int docId;
    public String URL;
    public String content;



    public String getURL(){
        return URL;
    }
    public SourceRecord(int f,String u, String tx){
        docId=f; URL=u;  content=tx;

    }
    public SourceRecord(int f,String u, String tt, String tx){
        docId=f; URL=u;  content=tx;


    }
    public int getId() {
        return docId;
    }



    public String getContent() {
        return content;
    }
}

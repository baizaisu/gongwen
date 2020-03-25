package com.example.gongwen.model;

public class SearchZFDataBean {

    private String fileName;
    private String filePath;
    private String contents;
//    public SearchZFDataBean(String fileName,String filePath,String contents){
//        this.fileName = fileName;
//        this.filePath = filePath;
//        this.contents = contents;
//
//    }

    public SearchZFDataBean(String fileName, String contents) {
        this.fileName = fileName;
        this.contents = contents;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
//    public String getFilePath() {
//        return filePath;
//    }
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    @Override
    public String toString() {
        //return "SearchZFDataBean [fileName=" + fileName + ", filePath=" + filePath + ", contents=" + contents + "]";
        return "SearchZFDataBean [fileName=" + fileName + ", contents=" + contents + "]";
    }
}


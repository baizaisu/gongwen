package com.example.gongwen.controller;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.example.gongwen.model.SearchZFDataBean;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wltea.analyzer.lucene.IKAnalyzer;

@RestController
public class LuceneIndexSearcherforZF {
    static String indexDir = "/Users/baifeng/eclipse/workspace/TestWebServiceRecommend1/src/data/LuceneDocZF";//索引目录
    public void LuceneIndexSearcher(){

    }

    @RequestMapping("/gongwen")
    public ArrayList<SearchZFDataBean> search(@RequestParam(value = "content")String content) throws Exception {

        TextRank textRank = new TextRank();
        String q = textRank.TextRank2(content);


        Directory dir = FSDirectory.open(Paths.get(indexDir)); //获取要查询的路径，也就是索引所在的位置
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
//       Analyzer analyzer = new StandardAnalyzer(); //标准分词器，会自动去掉空格啊，is a the等单词
        IKAnalyzer analyzer = new IKAnalyzer();
        QueryParser parser = new QueryParser("contents", analyzer); //查询解析器
        Query query = parser.parse(q); //通过解析要查询的String，获取查询对象

//       QueryParser parser1 = new QueryParser("filename", analyzer); //查询解析器
//       Query query1 = parser.parse("保护局"); //通过解析要查询的String，获取查询对象

//       Query query = new TermQuery(new Term("fullPath",q)); //精准查询
        //创建第一个查询条件


//     //现在的 使用方法 创建一个布尔查询对象
        BooleanQuery bq = new BooleanQuery.Builder()
                .add(query, Occur.MUST)
                .build();

        long startTime = System.currentTimeMillis(); //记录索引开始时间
        TopDocs docs = searcher.search(bq, 100);//开始查询，查询前10条数据，将记录保存在docs中

        long endTime = System.currentTimeMillis(); //记录索引结束时间
        System.out.println("匹配" + q + "共耗时" + (endTime - startTime) + "毫秒");
        System.out.println("查询到" + docs.totalHits + "条记录");
        //System.out.println("查询结果:" + docs.toString());
        ArrayList<SearchZFDataBean> sdbZFList = new ArrayList<SearchZFDataBean>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) { //取出每条查询结果
            Document doc = searcher.doc(scoreDoc.doc); //scoreDoc.doc相当于docID,根据这个docID来获取文档
            //String fullPath = doc.get("fullPath");
            //System.out.println(doc.get("fullPath")); //fullPath是刚刚建立索引的时候我们定义的一个字段
            System.out.println(doc.get("contents") + "\n");
            //sdbZFList.add(new SearchZFDataBean(doc.get("fileName"), fullPath,doc.get("contents")));
            sdbZFList.add(new SearchZFDataBean(doc.get("fileName"), doc.get("contents")));
        }
        reader.close();
        //System.out.println("当前公文的关键词是: " + q);
        return sdbZFList;
    }

    public static void main(String[] args) {
        LuceneIndexSearcherforZF luceneIndexSearcherforZF = new LuceneIndexSearcherforZF();
        try {
            luceneIndexSearcherforZF.search("民政部、国家体育总局部(局)务会议通过，并已经国务院批准，现予公布");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
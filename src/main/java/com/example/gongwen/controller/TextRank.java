package com.example.gongwen.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class TextRank {

//	public TextRank1() {
//		// TODO Auto-generated constructor stub
//	}


    public String TextRank2(String field) {

        List<String> keyWords = new ArrayList<>();
        int k = 5;  //窗口大小/2
        float d = 0.85f;
        /**
         * 标点符号、常用词、以及“名词、动词、形容词、副词之外的词”
         */
        Set<String> stopWordSet = new HashSet<String>();
        stopWordSet.add("是");
        stopWordSet.add("的");
        stopWordSet.add("地");
        stopWordSet.add("从");
        stopWordSet.add("将");
        stopWordSet.add("但");
        stopWordSet.add("都");
        stopWordSet.add("和");
        stopWordSet.add("为");
        stopWordSet.add("让");
        stopWordSet.add("在");
        stopWordSet.add("由");
        stopWordSet.add("上");
        //String field = "PageRank近似于一个用户，是指在Internet上随机地单击链接将会到达特定网页的可能性。通常，能够从更多地方到达的网页更为重要，因此具有更高的PageRank。每个到其他网页的链接，都增加了该网页的PageRank。具有较高PageRank的网页一般都是通过更多其他网页的链接而提高的。";


        Analyzer analyzer = new IKAnalyzer(true);
        TokenStream ts = null;
        //分词
        try {
            ts = analyzer.tokenStream("myfield", new StringReader(field));
            OffsetAttribute offset = (OffsetAttribute) ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute term = (CharTermAttribute) ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = (TypeAttribute) ts.addAttribute(TypeAttribute.class);
            ts.reset();

            while (ts.incrementToken()) {
                if (!stopWordSet.contains(term.toString())) {
                    keyWords.add(term.toString());
                }
            }
            ts.end();
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        Map<String, Set<String>> relationWords = new HashMap<>();


        //获取每个关键词 前后k个的组合
        for (int i = 0; i < keyWords.size(); i++) {
            String keyword = keyWords.get(i);
            Set<String> keySets = relationWords.get(keyword);
            if (keySets == null) {
                keySets = new HashSet<>();
                relationWords.put(keyword, keySets);
            }

            for (int j = i - k; j <= i + k; j++) {
                if (j < 0 || j >= keyWords.size() || j == i) {
                    continue;
                } else {
                    keySets.add(keyWords.get(j));
                }
            }
        }

       /* for (String s : relationWords.keySet()) {
            System.out.print(s+" ");
            for (String s1 : relationWords.get(s)) {
                System.out.print(s1+" ");
            }
            System.out.println();
        }*/


        Map<String, Float> score = new HashMap<>();
        float min_diff = 0.1f; //差值最小
        int max_iter = 100;//最大迭代次数

        //迭代
        for (int i = 0; i < max_iter; i++) {
            Map<String, Float> m = new HashMap<>();
            float max_diff = 0;
            for (String key : relationWords.keySet()) {
                Set<String> value = relationWords.get(key);
                //先给每个关键词一个默认rank值
                m.put(key, 1 - d);
                //一个关键词的TextRank由其它成员投票出来
                for (String other : value) {
                    int size = relationWords.get(other).size();
                    if (key.equals(other) || size == 0) {
                        continue;
                    } else {
                        m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                    }
                }
//                System.out.println("m.get(key):"+m.get(key)+" score:"+(score.get(key) == null ? 0 : score.get(key)));
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }

            score = m;
//            if (max_diff <= min_diff) {
//                System.out.println("迭代次数：" + i);
//                break;
//            }
        }

        List<Score> scores = new ArrayList<>();
        for (String s : score.keySet()) {
            Score score1 = new Score();
            score1.key = s;
            score1.significance = score.get(s);
            scores.add(score1);
        }

        scores.sort(new Comparator<Score>() {
            @Override
            public int compare(Score o1, Score o2) {
                if (o2.significance - o1.significance > 0) {
                    return 1;
                } else {
                    return -1;
                }

            }
        });

        //scores.subList(0, 3);
        //System.out.println(scores.subList(0, 3));


        //String[] KeyWord = new String[1000];
        //List<String> KeyWord = new ArrayList<String>();
        String Keyword = "";
        for (Score score1 : scores.subList(0, 3)) {
            //System.out.println(score1);
            //KeyWord.add(score1.toString());
            Keyword = Keyword + " " + score1.toString();
        }
        //return KeyWord;
//        System.out.println(KeyWord.subList(0, 3));
//        return KeyWord.subList(0, 2);
        return Keyword;
    }

    class Score {
        String key;
        float significance;

        @Override
        public String toString() {
            return key;
        }
    }


    public static void main(String[] args) {
        String field = "北京市房屋拆迁管理办法 　　现发布《北京市城市房屋拆迁管理办法》，自1998年12月1日起施行。 　　第一章 总则 　　第一条 为加强本市城市房屋拆迁管理，保障城市建设顺利进行，保护拆迁当事人的合法权益，根据国务院有关规定。结合本市实际情况，制定本办法。 　　第二条 凡在本市国有土地上，因城市建设需要拆迁房屋及其附属物（以下简称城市房屋拆迁），均适用本办法。 　　第三条 本市城市房屋拆迁，必须符合城市建设规划和有利于危旧房地区改建，适应城镇住房制度改革。 　　第四条 本办法所称拆迁人是指依法取得房屋拆迁许可证的建设单位或者个人。";
        //TextRank1 textRank1 = new TextRank1(field);
        //List<LinkedList<String>> textrank1 = new LinkedList<LinkedList<String>>(field);
        TextRank textRank1 = new TextRank();
        //textRank1.TextRank2(field);
        System.out.println(textRank1.TextRank2(field));
    }
}


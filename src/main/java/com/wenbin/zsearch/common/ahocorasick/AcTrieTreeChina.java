package com.wenbin.zsearch.common.ahocorasick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *   Ac自动机
 *
 *   @Author wenbin
 */
public class AcTrieTreeChina {

    /**
     * Ac节点
     */
    class AcTrieNode {

        /**
         * 字符
         */
        String word;

        /**
         * 是否有已匹配字符
         */
        boolean end;

        /**
         * 失配指针
         */
        AcTrieNode fail;

        /**
         * 字符id
         */
        int index;

        Map<Character, AcTrieNode> next = new HashMap<>();
    }

    private Map<String, Integer> targets;

    private AcTrieNode root;

    public AcTrieTreeChina(Map<String, Integer> targets) {
        this.targets = targets;
        this.root = new AcTrieNode();
        buildTireTree();
        buildAcFromTrie();
    }

    /**
     * 根据Trie树构建Ac自动机
     */
    private void buildAcFromTrie() {
        Queue<AcTrieNode> queue = new ArrayDeque<>();
        for (AcTrieNode node : root.next.values()) {
            if (node != null) {
                queue.add(node);
                node.fail = root;
            }
        }

        while (!queue.isEmpty()) {
            AcTrieNode node = queue.poll();
            for (Character c : node.next.keySet()) {
                queue.add(node.next.get(c));
                AcTrieNode failTo = node.fail;
                while (true) {
                    if (failTo == null) {
                        node.next.get(c).fail = root;
                        break;
                    }

                    if (failTo.next.get(c) != null) {
                        node.next.get(c).fail = failTo.next.get(c);
                        break;
                    } else {
                        failTo = failTo.fail;
                    }
                }
            }
        }
    }


    /**
     * 构建trie树
     */
    public void buildTireTree() {
        for (Map.Entry<String, Integer> word : targets.entrySet()) {
            AcTrieNode trie = root;
            for (int i = 0; i < word.getKey().length(); i++) {
                if (trie.next.get(word.getKey().charAt(i)) == null) {
                    trie.next.put(word.getKey().charAt(i), new AcTrieNode());
                }

                trie = trie.next.get(word.getKey().charAt(i));
            }

            trie.end = true;
            trie.word = word.getKey();
            trie.index = word.getValue();
        }
    }

    /**
     * 查询单词
     * @param text
     * @return
     */
    public Map<Integer, List<Integer>> search(String text) {
        Map<Integer, List<Integer>> result = new HashMap<>(16);
        AcTrieNode cur = root;
        int index = 0;
        while (index < text.length()) {
            char c = text.charAt(index);
            if (cur.next.get(c) != null) {
                cur = cur.next.get(c);
                if (cur.end) {
                    result.computeIfAbsent(cur.index, k -> new ArrayList<>());
                    result.get(cur.index).add(index - cur.word.length() + 1);
                }

                if (cur.fail != null && cur.fail.end) {
                    result.computeIfAbsent(cur.fail.index, k -> new ArrayList<>());
                    result.get(cur.fail.index).add(index - cur.fail.word.length() + 1);
                }

                index++;
            } else {
                cur = cur.fail;
                if (cur == null) {
                    cur = root;
                    index++;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("测试", 1);
        map.put("任命", 2);
        map.put("任务", 3);
        map.put("人人", 4);
        map.put("人民", 5);
        map.put("人民币", 6);
        map.put("人性", 7);
        map.put("人生", 8);
        map.put("人话", 9);
        map.put("哈哈", 10);
        map.put("电脑", 11);
        map.put("电视", 12);
        map.put("电机", 13);
        map.put("网络电话", 14);
        map.put("网络游戏", 15);
        map.put("网络开发", 16);
        map.put("网络电机", 17);

        AcTrieTreeChina ac = new AcTrieTreeChina(map);
        Map<Integer, List<Integer>> searchResult = ac.search("人性电机网络电机");
        assert searchResult.size() == 3;
    }
}

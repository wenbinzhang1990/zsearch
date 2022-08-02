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
public class AcTrieTree {

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

        AcTrieNode[] next = new AcTrieNode[26];
    }

    private List<String> targets;

    private AcTrieNode root;

    public AcTrieTree(List<String> targets) {
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
        for (AcTrieNode node : root.next) {
            if (node != null) {
                queue.add(node);
                node.fail = root;
            }
        }

        while (!queue.isEmpty()) {
            AcTrieNode node = queue.poll();
            for (int i = 0; i < node.next.length; i++) {
                if (node.next[i] != null) {
                    queue.add(node.next[i]);
                    AcTrieNode failTo = node.fail;
                    while (true) {
                        if (failTo == null) {
                            node.next[i].fail = root;
                            break;
                        }

                        if (failTo.next[i] != null) {
                            node.next[i].fail = failTo.next[i];
                            break;
                        } else {
                            failTo = failTo.fail;
                        }
                    }
                }
            }
        }
    }

    /**
     * 构建trie树
     */
    public void buildTireTree() {
        for (String word : targets) {
            AcTrieNode trie = root;
            for (int i = 0; i < word.length(); i++) {
                if (trie.next[word.charAt(i) - 'a'] == null) {
                    trie.next[word.charAt(i) - 'a'] = new AcTrieNode();
                }

                trie = trie.next[word.charAt(i) - 'a'];
            }

            trie.end = true;
            trie.word = word;
        }
    }

    /**
     * 查询单词
     * @param text
     * @return
     */
    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> result = new HashMap<>(16);
        for (String word : targets) {
            result.put(word, new ArrayList<>());
        }

        AcTrieNode cur = root;
        int index = 0;
        while (index < text.length()) {
            int c = text.charAt(index) - 'a';
            if (cur.next[c] != null) {
                cur = cur.next[c];
                if (cur.end) {
                    result.get(cur.word).add(index - cur.word.length() + 1);
                }

                if (cur.fail != null && cur.fail.end) {
                    result.get(cur.fail.word).add(index - cur.fail.word.length() + 1);
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


    /**
     * 查询单词
     * @param text
     * @return
     */
    public Map<String, Integer> search(char[] text, int begin) {
        AcTrieNode cur = root;
        Map<String, Integer> result = new HashMap<>();
        for (int index = begin; index < text.length; index++) {
            int originBegin = index;
            while (cur != null) {
                int c = text[index++] - 'a';
                if (cur.next[c] != null) {
                    cur = cur.next[c];
                    if (cur.end) {
                        result.put(cur.word, originBegin);
                        return result;
                    }

                    if (cur.fail != null && cur.fail.end) {
                        result.put(cur.fail.word, originBegin);
                        return result;
                    }
                } else {
                    cur = cur.fail;
                }
            }

            cur = root;
        }

        return null;
    }

}

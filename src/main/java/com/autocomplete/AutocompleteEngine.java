package com.autocomplete;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class AutocompleteEngine {
    private TrieNode root = new TrieNode();
    private static final int MAX_EDIT_DISTANCE = 2;

    public void loadDictionary(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addWord(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return results;
        }
        findWords(node, results, prefix);
        return results;
    }

    private void findWords(TrieNode node, List<String> results, String prefix) {
        if (node.isEndOfWord) results.add(prefix);
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findWords(entry.getValue(), results, prefix + entry.getKey());
        }
    }

    public List<String> autocompleteWithTypos(String input) {
        TreeSet<String> suggestions = new TreeSet<>();
        for (String word : getAllWords(root, "")) {
            if (editDistance(input, word) <= MAX_EDIT_DISTANCE) {
                suggestions.add(word);
            }
        }
        return new ArrayList<>(suggestions);
    }

    private List<String> getAllWords(TrieNode node, String prefix) {
        List<String> words = new ArrayList<>();
        if (node.isEndOfWord) words.add(prefix);
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            words.addAll(getAllWords(entry.getValue(), prefix + entry.getKey()));
        }
        return words;
    }

    private int editDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        for (int i = 0; i <= str1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= str2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[str1.length()][str2.length()];
    }
}

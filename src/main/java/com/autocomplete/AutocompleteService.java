package com.autocomplete;

import com.google.gson.Gson;

import spark.Spark;

public class AutocompleteService {
    public static void main(String[] args) {
        AutocompleteEngine engine = new AutocompleteEngine();
        engine.loadDictionary("path/to/dictionary.txt"); // Update with your dictionary path

        Spark.port(4567); // Port where the server runs

        // Serve static files from the "public" directory
        Spark.staticFiles.externalLocation("public");

        Spark.get("/autocomplete", (req, res) -> {
            String prefix = req.queryParams("prefix");
            if (prefix == null || prefix.isEmpty()) {
                res.status(400);
                return "Prefix parameter is missing";
            }
            return new Gson().toJson(engine.autocomplete(prefix));
        });

        Spark.get("/autocompleteWithTypos", (req, res) -> {
            String input = req.queryParams("input");
            if (input == null || input.isEmpty()) {
                res.status(400);
                return "Input parameter is missing";
            }
            return new Gson().toJson(engine.autocompleteWithTypos(input));
        });
    }
}

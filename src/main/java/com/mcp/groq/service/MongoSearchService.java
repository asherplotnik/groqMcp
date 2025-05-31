package com.mcp.groq.service;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoSearchService {

    private  final MongoDatabase database;

    public List<Document> search(String collectionName, String fieldName, String fieldValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document filter = new Document(fieldName, fieldValue);
        List<Document> results = new ArrayList<>();
        collection.find(filter).into(results);
        return results;
    }

}

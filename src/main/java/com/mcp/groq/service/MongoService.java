package com.mcp.groq.service;


import com.mcp.groq.dto.UserDocument;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoService {

    private  final MongoDatabase database;

    public List<Document> search(String collectionName, String fieldName, String fieldValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document filter = new Document(fieldName, fieldValue);
        List<Document> results = new ArrayList<>();
        collection.find(filter).into(results);
        return results;
    }

    public List<Document> update(String collectionName, UserDocument userDocument) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document filter = new Document("userId", userDocument.getUserId());
        List<Document> results = new ArrayList<>();
        collection.find(filter).into(results);
        Document persistent = toDocument(userDocument);
        if (!CollectionUtils.isEmpty(results)) {
            collection.updateOne(filter, persistent);
        } else {
            collection.insertOne(persistent);
        }
        return List.of(persistent);
    }

    private Document toDocument(UserDocument userDocument) {
        Document persistent = new Document();
        persistent.put("userId", userDocument.getUserId());
        persistent.put("birthYear", userDocument.getBirthYear());
        persistent.put("firstNAme", userDocument.getFirstName());
        persistent.put("sureName", userDocument.getSureName());
        return persistent;
    }

}

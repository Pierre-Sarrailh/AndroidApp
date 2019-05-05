package com.example.pierre.rainman;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.internal.ChangeEvent;

import org.bson.BsonValue;
import org.bson.Document;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

// Base Stitch Packages
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
// Stitch Authentication Packages
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
// MongoDB Service Packages
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
// Utility Packages
import com.mongodb.stitch.core.internal.common.BsonUtils;

public class MainActivity extends AppCompatActivity {
    RemoteMongoCollection _remoteCollection;
    protected Button button;
    protected Button button2;
    protected Button toMap;
    protected Button post;
    protected EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this sets up the Stitch client connecting to my atlas DB with my id (stitchrainman-ahumz)
        //this then creates my default DB and collecion making the posts to the DB anonymous
        final StitchAppClient client = Stitch.initializeAppClient("stitchrainman-ahumz");
        client.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
            @Override
            public void onComplete(@NonNull Task<StitchUser> task) {
                final RemoteMongoClient remoteMongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
                _remoteCollection = remoteMongoClient.getDatabase("rainman").getCollection("rainers");
            }
        });

        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document time = new Document().append("this", "is").append("a", "test");
                _remoteCollection.insertOne(time);
            }
        });

        toMap = (Button)findViewById(R.id.toMap);

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MapScreen.class));
            }
        });

        post = (Button)findViewById(R.id.buttonPost);
        name = (EditText)findViewById(R.id.name);
        String result = name.getText().toString();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document entry = new Document().append("name", result);
                _remoteCollection.insertOne(entry);
            }
        });

        button2 = (Button)findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteFindIterable results = _remoteCollection.find();
                Toast.makeText(getApplicationContext(), (CharSequence) results, Toast.LENGTH_LONG).show();
            }
        });



        /*
        //this is where I implement crud to my DB
        _remoteCollection.sync().configure(
                DefaultSyncConflictResolvers.remoteWins(),
                new MyUpdateListener(),
                new MyErrorListener()
        );
        

        Document canvas = new Document("item","canvas")
                .append("qty",100)
                .append("tags", 100);

        Document size = new Document("h",28)
                .append("w", 33.2)
                .append("df", 23);

        canvas.put("size", size);
        final Task<RemoteInsertOneResult> res = _remoteCollection.sync().insertOne(canvas);

        res.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "succcesss", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
                }
            }

        });
        */
    }
    /*
    private class MyErrorListener implements ErrorListener{
        @Override
        public void onError(BsonValue documentId, Exception error){
            Log.e("Stitch", error.getLocalizedMessage());
            Set<BsonValue> docsThatNeedToBeFixed = _remoteCollection.sync().getPausedDocumentIds();
            for (BsonValue doc_id : docsThatNeedToBeFixed){
                //say when error is resolved
                _remoteCollection.sync().resumeSyncForDocument(doc_id);
            }
            //refresh the app view
        }
    }

    private class MyUpdateListener implements ChangeEventListener<Document> {
        @Override public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event){
            if (!event.hasUncommittedWrites()){

            }
        }
    }
    */
}

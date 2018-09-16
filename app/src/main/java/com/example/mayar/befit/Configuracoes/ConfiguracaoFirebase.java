package com.example.mayar.befit.Configuracoes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by lylly on 03/05/2018.
 */

public final class ConfiguracaoFirebase {
    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFirebaseDatabase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;
    }

    public static boolean isUserSignedIn(FirebaseUser user){
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }
}

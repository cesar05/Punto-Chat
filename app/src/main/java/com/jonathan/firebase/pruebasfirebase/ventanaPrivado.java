package com.jonathan.firebase.pruebasfirebase;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ventanaPrivado extends AppCompatActivity implements FirebaseAuth.AuthStateListener, RecyclerView.OnItemTouchListener {

    private FirebaseAuth mAuth;
    private DatabaseReference referenciaPrivados;
    private DatabaseReference referenciachild;
    private DatabaseReference referenciachildR;
    private Button mSendButton;
    private EditText mMessageEdit;
    private CircleImageView fotoUsuario;
    private String emailP, uidP, url, tokenP, nombreP;
    private RecyclerView mMessages;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<mensajePrivado, ChatHolder> mRecyclerViewAdapter;
    TextView nombre, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_privado);

        nombre = (TextView) findViewById(R.id.msjContacto);
        email = (TextView) findViewById(R.id.emailcon);
        fotoUsuario = (CircleImageView) findViewById(R.id.imagenPrivado);
        Bundle bundle = getIntent().getExtras();
        nombre.setText(bundle.getString("nombreContacto"));
        email.setText(bundle.getString("emailContacto"));


        nombreP = bundle.getString("nombreContacto");
        emailP = bundle.getString("emailContacto");
        uidP = bundle.getString("idContacto");
        url = bundle.getString("imagenContacto");
        tokenP = bundle.getString("tokenContacto");

        if (!url.equals("null")){
            Glide.with(this).load(url).into(fotoUsuario);
        }else{
            Glide.with(this).load("http://www.freeiconspng.com/uploads/account-icon-33.png").into(fotoUsuario);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        mSendButton = (Button) findViewById(R.id.btnEnviar);
        mMessageEdit = (EditText) findViewById(R.id.mensajePrivado);

        referenciaPrivados = FirebaseDatabase.getInstance().getReference();
        referenciachild = referenciaPrivados.child("mensajesPrivados").child(mAuth.getCurrentUser().getUid());
        referenciachildR = referenciaPrivados.child("mensajesPrivados").child(uidP);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mAuth.getCurrentUser().getDisplayName();
                String name = ""+uid+ "\n" +getTimeStamp();
                //String name = "User " + uid.substring(0, 6);
                mensajePrivado mensajeP = new mensajePrivado(name, mAuth.getCurrentUser().getEmail(), emailP, mMessageEdit.getText().toString(), mAuth.getCurrentUser().getUid());
                referenciachild.push().setValue(mensajeP);

                mensajePrivado mensajePP = new mensajePrivado(name, emailP, mAuth.getCurrentUser().getEmail(), mMessageEdit.getText().toString(), mAuth.getCurrentUser().getUid());
                referenciachildR.push().setValue(mensajePP);

                enviarNotificacion(mMessageEdit.getText().toString());
                mMessageEdit.setText("");
            }
        });

        mMessages = (RecyclerView) findViewById(R.id.listaMensajesPrivados);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);
    }

    public static class mensajePrivado {
        String nombreM;
        String emailEnvia;
        String emailRecibe;
        String mensaje;
        String uid;
        public mensajePrivado(){}

        public mensajePrivado (String nombreM, String emailEnvia, String emailRecibe, String mensaje, String uid){
            this.nombreM = nombreM;
            this.emailEnvia = emailEnvia;
            this.emailRecibe = emailRecibe;
            this.mensaje = mensaje;
            this.uid = uid;
        }

        public String getNombreM() {
            return nombreM;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getEmailRecibe() {
            return emailRecibe;
        }

        public String getEmailEnvia() {
            return emailEnvia;
        }

        public String getUid() {
            return uid;
        }

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
    }

    public void updateUI() {
        // Sending only allowed when signed in
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());
    }



    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public void onStart() {
        super.onStart();
        // Default Database rules do not allow unauthenticated reads, so we need to
        // sign in before attaching the RecyclerView adapter otherwise the Adapter will
        // not be able to read any data from the Database.
        if (!isSignedIn()) {
           Log.d("OFFLINE", "No esta logueado");
        } else {

            attachRecyclerViewAdapter();
            //attachRecyclerViewAdapter(prueba);
        }
    }

    public boolean isSignedIn() {
        return (mAuth.getCurrentUser() != null);
    }

    private void attachRecyclerViewAdapter() {
        Query lastFifty = referenciachild.orderByChild("emailRecibe").equalTo(emailP);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<mensajePrivado, ChatHolder>(
                mensajePrivado.class, R.layout.message, ChatHolder.class, lastFifty) {

            @Override
            public void populateViewHolder(ChatHolder chatView, mensajePrivado mensajePri, int position) {
                chatView.setName(mensajePri.getNombreM());
                chatView.setText(mensajePri.getMensaje());
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && mensajePri.getUid().equals(currentUser.getUid())) {
                    chatView.setIsSender(true);
                } else {
                    chatView.setIsSender(false);
                }
            }
        };

        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                mManager.smoothScrollToPosition(mMessages, null, mRecyclerViewAdapter.getItemCount());
            }
        });

        mMessages.setAdapter(mRecyclerViewAdapter);

    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setIsSender(Boolean isSender) {
            FrameLayout left_arrow = (FrameLayout) mView.findViewById(R.id.left_arrow);
            FrameLayout right_arrow = (FrameLayout) mView.findViewById(R.id.right_arrow);
            RelativeLayout messageContainer = (RelativeLayout) mView.findViewById(R.id.message_container);
            LinearLayout message = (LinearLayout) mView.findViewById(R.id.message);

            int color;
            if (isSender) {
                left_arrow.setVisibility(View.GONE);
                right_arrow.setVisibility(View.VISIBLE);
                messageContainer.setGravity(Gravity.END);
                message.setBackgroundResource(R.drawable.msjsalida);
            } else {
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.GONE);
                messageContainer.setGravity(Gravity.START);
                message.setBackgroundResource(R.drawable.msjentrada);
            }
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.name_text);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.message_text);
            field.setText(text);
        }
    }

    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yy ");
        return format.format(new Date());
    }

    public void enviarNotificacion(String mensaje)
    {
        RequestQueue queue  = Volley.newRequestQueue(this);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        /*
        String body="{ \"data\": {\n" +
                "    \"msj\": \"hola como estas?\"\n" +
                "  },\n" +
                "  \"to\" : \"c89aR7reC04:APA91bGWzHzWqRLm-snkr6hzTfKsr18HUiWTOWm8O062sh-QxtDHqCAvCeMSUKla5o034_Op9ulShtRFGFmYAzg9rvwK_WzHX9fZSbuqWwNsK6lCMCoIWak2AoXV54y55Hh_m1gGxp9e\"\n" +
                "}\t";
                */
        String body="{ \"data\": {\n" +
                "    \"de\": \""+mAuth.getCurrentUser().getDisplayName()+"\"\n" +
                "    ,\"msj\": \""+mensaje+"\"\n" +
                "  },\n" +
                "  \"to\" : \""+tokenP+"\"\n" +
                "}\t";
        JsonObjectRequest request2 = new JsonObjectRequest(
                Request.Method.POST, "https://fcm.googleapis.com/fcm/send",body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("enviarNotificacion","respuesta"+ response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("enviarNotificacion", "error "+error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key=AIzaSyAF41lcdqrf0cKD7G7R8wb-nqwbfWRfc30");
                return params;
            }
        };
        queue.add(request2);
    }
}

package com.suman.trucksharing;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.pay.PayClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.PaymentData;
import com.google.maps.android.PolyUtil;
import com.suman.trucksharing.model.DirectionResponses;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionListener;
import com.suman.trucksharing.speechrec.OnSpeechRecognitionPermissionListener;
import com.suman.trucksharing.speechrec.SpeechRecognition;
import com.suman.trucksharing.viewmodel.CheckoutViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Direction extends AppCompatActivity implements OnMapReadyCallback, OnSpeechRecognitionPermissionListener, OnSpeechRecognitionListener {
    private GoogleMap map;
    private LatLng fkip;
    private LatLng monas;
    TextView pickup, dropoff, orderamt;
    int amount;
    Button book, call;
    SpeechRecognition speechRecognition;
    TextToSpeech tts;
    private CheckoutViewModel model;

    private static final int ADD_TO_GOOGLE_WALLET_REQUEST_CODE = 999;

    private View googlePayButton;
    private View addToGoogleWalletButtonContainer;
    private View addToGoogleWalletButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        pickup = findViewById(R.id.txt_ploc);
        dropoff = findViewById(R.id.txt_droploc);
        book = findViewById(R.id.btn_book);
        call = findViewById(R.id.btn_call);
        orderamt = findViewById(R.id.order_amount);
        initgooglepay();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener( this);
        speechRecognition.setSpeechRecognitionListener( this);
        Intent intent = getIntent();
        try {
            pickup.setText(intent.getStringExtra("picklocation"));
            dropoff.setText(intent.getStringExtra("droplocation"));
            String[] fromLatLng = intent.getStringExtra("from").split(",");
            String[] toLatLng = intent.getStringExtra("to").split(",");
            fkip = new LatLng(Double.parseDouble(fromLatLng[0]), Double.parseDouble(fromLatLng[1]));
            monas = new LatLng(Double.parseDouble(toLatLng[0]), Double.parseDouble(toLatLng[1]));
            amount = intent.getIntExtra("amount", 0);
            orderamt.setText("Approx. Fare: $"+amount);
        }catch (Exception e){
            e.printStackTrace();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        book.setOnClickListener(this::requestPayment);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               phoneCall();

            }
        });
    }

    private void phoneCall() {
        String[] permission = {android.Manifest.permission.CALL_PHONE};
        if (ContextCompat.checkSelfPermission(Direction.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Direction.this, permission,
                    00);

        } else {
            // else block means user has already accepted.And make your phone call here.
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+12319258360"));
            speechRecognition.stopSpeechRecognition();
            startActivity(intent);

        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        MarkerOptions markerFkip = new MarkerOptions()
                .position(fkip)
                .title("From");
        MarkerOptions markerMonas = new MarkerOptions()
                .position(monas)
                .title("To");

        map.addMarker(markerFkip);
        map.addMarker(markerMonas);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(monas, 11.6f));

        String fromFKIP = String.valueOf(fkip.latitude) + "," + String.valueOf(fkip.longitude);
        String toMonas = String.valueOf(monas.latitude) + "," + String.valueOf(monas.longitude);

        ApiServices apiServices = RetrofitClient.apiServices(this);
        apiServices.getDirection(fromFKIP, toMonas, getString(R.string.api_key))
                .enqueue(new Callback<DirectionResponses>() {
                    @Override
                    public void onResponse(@NonNull Call<DirectionResponses> call, @NonNull Response<DirectionResponses> response) {
                        drawPolyline(response);
                        Log.d("bisa dong oke", response.message());
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionResponses> call, @NonNull Throwable t) {
                        Log.e("anjir error", t.getLocalizedMessage());
                    }
                });
    }


    private void drawPolyline(@NonNull Response<DirectionResponses> response) {
        if (response.body() != null) {
            String shape = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
            PolylineOptions polyline = new PolylineOptions()
                    .addAll(PolyUtil.decode(shape))
                    .width(8f)
                    .color(Color.RED);
            map.addPolyline(polyline);
        }
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void OnSpeechRecognitionStarted() {

    }

    @Override
    public void OnSpeechRecognitionStopped() {

    }

    @Override
    public void OnSpeechRecognitionFinalResult(String finalSentence) {
        if (finalSentence.toLowerCase().contains("google")) {
            if (finalSentence.toLowerCase().contains("book")) {
                tts.speak("Ok booking your order", TextToSpeech.QUEUE_FLUSH, null);
                book.setPressed(true);
                book.setPressed(false);
            } else if (finalSentence.toLowerCase().contains("call")) {
                tts.speak("Calling to the driver", TextToSpeech.QUEUE_FLUSH, null);
                phoneCall();
            } else if (finalSentence.toLowerCase().contains("back")) {
                tts.speak("Ok going back", TextToSpeech.QUEUE_FLUSH, null);
                speechRecognition.stopSpeechRecognition();
                onBackPressed();
            }
        }
        resetRecog();

    }

    @Override
    public void OnSpeechRecognitionCurrentResult(String currentWord) {

    }

    @Override
    public void OnSpeechRecognitionError(int errorCode, String errorMsg) {
        resetRecog();
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        Call<DirectionResponses> getDirection(@Query("origin") String origin,
                                              @Query("destination") String destination,
                                              @Query("key") String apiKey);
    }

    private static class RetrofitClient {
        static ApiServices apiServices(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .build();

            return retrofit.create(ApiServices.class);
        }
    }
    //GOOGLE PAY API
    ActivityResultLauncher<IntentSenderRequest> resolvePaymentForResult = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case Activity.RESULT_OK:
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            PaymentData paymentData = PaymentData.getFromIntent(result.getData());
                            if (paymentData != null) {
                                handlePaymentSuccess(paymentData);
                            }
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;
                }
            });
    private void initgooglepay() {
        model = new ViewModelProvider(this).get(CheckoutViewModel.class);
        model.canUseGooglePay.observe(this, this::setGooglePayAvailable);
    }
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            //googlePayButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.google_pay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }
    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        //googlePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        long dummyPriceCents = amount * 100L;
        long shippingCostCents = 900;
        long totalPriceCents = dummyPriceCents + shippingCostCents;
        final Task<PaymentData> task = model.getLoadPaymentDataTask(totalPriceCents);

        task.addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                handlePaymentSuccess(completedTask.getResult());
            } else {
                Exception exception = completedTask.getException();
                if (exception instanceof ResolvableApiException) {
                    PendingIntent resolution = ((ResolvableApiException) exception).getResolution();
                    resolvePaymentForResult.launch(new IntentSenderRequest.Builder(resolution).build());

                } else if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    handleError(apiException.getStatusCode(), apiException.getMessage());

                } else {
                    handleError(CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!");
                }
            }

            // Re-enables the Google Pay payment button.
            //googlePayButton.setClickable(true);
        });
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, getString(R.string.payments_show_name, billingName),
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));

        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode holds the value of any constant from CommonStatusCode or one of the
     *               WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(int statusCode, @Nullable String message) {
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TO_GOOGLE_WALLET_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK: {
                    Toast
                            .makeText(this, getString(R.string.add_google_wallet_success), Toast.LENGTH_LONG)
                            .show();
                    break;
                }

                case RESULT_CANCELED: {
                    // Save canceled
                    break;
                }

                case PayClient.SavePassesResult.SAVE_ERROR: {
                    if (data != null) {
                        String apiErrorMessage = data.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE);
                        handleError(resultCode, apiErrorMessage);
                    }
                    break;
                }

                default: handleError(
                        CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                " exception when trying to deliver the task result to an activity!"
                );
            }

            addToGoogleWalletButton.setClickable(true);
        }
    }


    private void resetRecog() {
        speechRecognition.startSpeechRecognition();
    }

    @Override
    protected void onPause() {
        Log.e("SIGNUP", "onPause: " );
        speechRecognition.stopSpeechRecognition();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        speechRecognition.startSpeechRecognition();
        Log.e("TAG", "onPostResume: " );
        super.onPostResume();
    }
}
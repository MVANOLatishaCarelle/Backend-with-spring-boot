package com.example.api_springboot.controller;

import org.aspectj.weaver.ast.Call;

import com.example.api_springboot.modele.Commande;
import com.example.api_springboot.modele.Plat;

public class check {
@Override
public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
    if (response.isSuccessful() && response.body() != null) {
        String token = response.body().getToken();
        authManager.saveAuthToken(token);
        
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        
        // Simply finish the activity to return to previous screen
        setResult(RESULT_OK);
        finish();
    } else {
        handleErrorResponse(response);
    }
}



// In your order placement method
private void placeOrder(Plat plat, int quantity) {
    if (authManager.getAuthToken() == null) {
        // Launch login activity and retry order after login
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        pendingOrder = new OrderRequest(plat, quantity); // Store the pending order
    } else {
        // Proceed with order directly
        executeOrder(plat, quantity);
    }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
        if (pendingOrder != null) {
            // Retry the pending order after successful login
            executeOrder(pendingOrder.getPlat(), pendingOrder.getQuantity());
            pendingOrder = null;
        }
    }
}

private void executeOrder(Plat plat, int quantity) {
    commandHelper.placeOrder(plat, quantity, new CommandCallback() {
        @Override
        public void onSuccess(Commande commande) {
            runOnUiThread(() -> {
                Toast.makeText(PlatListActivity.this,
                        "Order #" + commande.getId() + " placed!",
                        Toast.LENGTH_SHORT).show();
                currentQuantity = 1;
            });
        }

        @Override
        public void onFailure(String errorMessage) {
            runOnUiThread(() -> {
                Toast.makeText(PlatListActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
                
                // Special case: if token is invalid, prompt login again
                if (errorMessage.contains("unauthorized") || 
                    errorMessage.contains("token")) {
                    authManager.clearAuthToken();
                    placeOrder(plat, quantity); // This will now trigger login flow
                }
            });
        }
    });
}




    public class OrderRequest {
    private Plat plat;
    private int quantity;
    
    // Constructor, getters
    public OrderRequest(Plat plat, int quantity) {
        this.plat = plat;
        this.quantity = quantity;
    }
    
    public Plat getPlat() { return plat; }
    public int getQuantity() { return quantity; }
}



Why This Approach is Better:
Separation of Concerns:

LoginActivity only handles authentication

Order logic stays in the activity that manages orders

Simpler Flow:

No need for complex SharedPreferences storage

Uses Android's built-in activity result system

Maintains All Features:

Still handles pending orders

Properly manages auth token expiration

Clean error handling

More Maintainable:

Logic is where you'd expect to find it

Easier to modify order flow without touching login code

Better User Experience:

Returns user to exactly where they were before login

Automatically retries the order
}

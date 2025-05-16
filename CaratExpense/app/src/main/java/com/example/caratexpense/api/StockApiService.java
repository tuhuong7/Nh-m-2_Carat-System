package com.example.caratexpense.api;

import com.example.caratexpense.models.Stock;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockApiService {
    // Định nghĩa phương thức API để lấy danh sách cổ phiếu
    @GET("v3/stock/list")
    Call<List<Stock>> getStockList(@Query("apikey") String apiKey);
}

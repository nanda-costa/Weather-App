package com.example.findinglogs.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.repo.Repository;
import com.example.findinglogs.model.repo.remote.api.WeatherCallback;
import com.example.findinglogs.model.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private static final int FETCH_INTERVAL = 120_000;
    private final Repository mRepository;
    private final MutableLiveData<List<Weather>> _weatherList = new MutableLiveData<>(new ArrayList<>());
    private final LiveData<List<Weather>> weatherList = _weatherList;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable fetchRunnable = this::fetchAllForecasts;

    public MainViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        startFetching();
    }

    public LiveData<List<Weather>> getWeatherList() {
        return weatherList;
    }

    private void startFetching() {
        fetchAllForecasts();
        handler.postDelayed(fetchRunnable, FETCH_INTERVAL);
    }

    // Mudei de private para public
    public void fetchAllForecasts() {
        if (Logger.ISLOGABLE) Logger.d(TAG, "fetchAllForecasts()");
        // Remover callbacks existentes para evitar múltiplas chamadas enquanto atualiza
        handler.removeCallbacks(fetchRunnable);

        HashMap<String, String> localizations = mRepository.getLocalizations();
        // Criei uma nova lista vazia para armazenar os dados atualizados
        List<Weather> updatedList = new ArrayList<>();
        // Usei um contador para saber quando todas as chamadas foram concluídas
        final int[] completedCalls = {0};

        for (String latlon : localizations.values()) {
            mRepository.retrieveForecast(latlon, new WeatherCallback() {
                @Override
                public void onSuccess(Weather result) {
                    synchronized (updatedList) { // Sincroniza para evitar problemas de concorrência
                        // Verifique se já existe um item para esta cidade e atualize-o,
                        // ou adicione se for novo.
                        // Para simplificar, irei substituir o item se já existir,
                        // ou adicionar se não existir.
                        boolean found = false;
                        for (int i = 0; i < updatedList.size(); i++) {
                            if (updatedList.get(i).getName().equals(result.getName())) {
                                updatedList.set(i, result); // Atualiza o item existente
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            updatedList.add(result); // Adiciona se for um novo item
                        }
                    }
                    completedCalls[0]++;
                    if (completedCalls[0] == localizations.size()) {
                        // Todas as chamadas foram concluídas, atualize o LiveData
                        _weatherList.postValue(updatedList); // Use postValue para threads em segundo plano
                        handler.postDelayed(fetchRunnable, FETCH_INTERVAL); // Reinicia o agendamento
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (Logger.ISLOGABLE) Logger.e(TAG, "Failed to fetch weather: " + error);
                    completedCalls[0]++;
                    if (completedCalls[0] == localizations.size()) {
                        // Mesmo se houver falhas, tente atualizar o LiveData com o que foi coletado
                        // ou apenas reinicie o agendamento
                        _weatherList.postValue(updatedList); // Pode enviar uma lista parcial ou vazia se tudo falhar
                        handler.postDelayed(fetchRunnable, FETCH_INTERVAL); // Reinicia o agendamento
                    }
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        handler.removeCallbacks(fetchRunnable);
        super.onCleared();
    }

    // Este método não é necessário para o refresh do botão, pois fetchAllForecasts já faz isso.
    // public void retrieveForecast(String latLon, WeatherCallback callback) {
    //     mRepository.retrieveForecast(latLon, callback);
    // }
}
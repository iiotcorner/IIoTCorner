package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class Estadistico extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    TextView infoIn, tvEntrada, tvDias, tvCiclos, tvON, tvkW;
    String label="-", snack = "Sin datos para: ", nombre;
    Spinner Rango;
    private PieChart chart;
    private BarChart barChart;
    ProgressDialog pdvalidando;
    RelativeLayout relativeLayout1;

    ArrayList<String> Tiempos_01 = new ArrayList<>(); ArrayList<String> Tiempos_02 = new ArrayList<>();
    ArrayList<String> Tiempos_03 = new ArrayList<>(); ArrayList<String> Tiempos_04 = new ArrayList<>();
    ArrayList<Double> Ons = new ArrayList<>(); ArrayList<Double> Offs = new ArrayList<>();
    ArrayList<Integer> Contador01 = new ArrayList<>(); ArrayList<Integer> Contador02 = new ArrayList<>();
    ArrayList<Integer> Contador03 = new ArrayList<>(); ArrayList<Integer> Contador04 = new ArrayList<>();
    int lapso, milis;

    Button btn01, btn02, btn03, btn04;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistico);

        infoIn = findViewById(R.id.tvhoras);
        tvEntrada = findViewById(R.id.textViewentrada);
        tvDias = findViewById(R.id.textView_dias);
        tvCiclos = findViewById(R.id.textView_ciclos);
        tvON = findViewById(R.id.textView_on);
        tvkW = findViewById(R.id.textView_kw);
        relativeLayout1 = findViewById(R.id.rlayout1);

        btn01 = findViewById(R.id.btn1); btn02 = findViewById(R.id.btn2);
        btn03 = findViewById(R.id.btn3); btn04 = findViewById(R.id.btn4);

        database = FirebaseDatabase.getInstance();
        pdvalidando = new ProgressDialog(this);
        ENTRADAS("1"); ENTRADAS("2"); ENTRADAS("3"); ENTRADAS("4");

        Rango = findViewById(R.id.spinhrs);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Lista_horas_estd, android.R.layout.simple_spinner_item);
        Rango.setAdapter(adapter);
        Rango.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        lapso = 24; milis = 3600000;
                        CALCULOS(view);
                        label = "Ciclos por hora";
                        break;
                    case 1:
                        lapso = 48; milis = 3600000;
                        CALCULOS(view);
                        label = "Ciclos por hora";
                        break;
                    case 2:
                        lapso = 7; milis = 24*3600000;
                        CALCULOS(view);
                        label = "Ciclos por dia";
                        break;
                    case 3:
                        lapso = 15; milis = 24*3600000;
                        CALCULOS(view);
                        label = "Ciclos por dia";
                        break;
                    case 4:
                        lapso = 30; milis = 24*3600000;
                        CALCULOS(view);
                        label = "Ciclos por dia";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // PARTE GRAFICA MEDIA TORTA
        chart = findViewById(R.id.chart1);
        chart.setBackgroundColor(Color.WHITE);
        moveOffScreen();

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setCenterText(infocentral());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        chart.setMaxAngle(180f); // HALF CHART
        chart.setRotationAngle(180f);
        chart.setCenterTextOffset(0, -20);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        //PARTE GRAFICA BAR CHART:
        barChart = findViewById(R.id.barchart1);
        //barChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(24);

        Legend l2 = barChart.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setForm(Legend.LegendForm.CIRCLE);
        l2.setFormSize(9f);
        l2.setTextSize(11f);
        l2.setXEntrySpace(4f);


    }

    private SpannableString infocentral() {
        SpannableString s = new SpannableString("IIoT Corner\nSimplifying Industrial IoT");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 11, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 12, s.length() - 14, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 12, s.length() - 14, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 12, s.length() - 14, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }


    private void moveOffScreen() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int offset = (int)(height * 0.25); /* percent to move */
        RelativeLayout.LayoutParams rlParams =  (RelativeLayout.LayoutParams) chart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(rlParams);
    }

    private void ENTRADAS(final String in) {
        // Obteniendo datos de la base de datos>
        pdvalidando.setMessage("Obteniendo Datos\nPor favor espere...");
        pdvalidando.show();
        String path = ("in"+in+"/cambio");  myRef = database.getReference(path);
        String path2 = ("in"+in+"/nombre"); myRef2 = database.getReference(path2);

        // Names of signals:
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nombre = snapshot.getValue().toString().toUpperCase();
                switch (in){
                    case "1":
                        btn01.setText(nombre);
                        break;
                    case "2":
                        btn02.setText(nombre);
                        break;
                    case "3":
                        btn03.setText(nombre);
                        break;
                    case "4":
                        btn04.setText(nombre);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firebase: " + error);
            }
        });

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    System.out.println(dataSnapshot.getChildrenCount());
                    //Tiempos_01.clear(); Tiempos_02.clear(); Tiempos_03.clear(); Tiempos_04.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String tiempo = ds.getValue().toString();
                        switch (in){
                            case "1":
                                Tiempos_01.add(tiempo);
                                if (Tiempos_01.size()>=dataSnapshot.getChildrenCount()){
                                    CALCULAR(Tiempos_01, "1");
                                }
                                break;
                            case "2":
                                Tiempos_02.add(tiempo);
                                if (Tiempos_02.size()>=dataSnapshot.getChildrenCount()){
                                    CALCULAR(Tiempos_02, "2");
                                }
                                break;
                            case "3":
                                Tiempos_03.add(tiempo);
                                if (Tiempos_03.size()>=dataSnapshot.getChildrenCount()){
                                    CALCULAR(Tiempos_03, "3");
                                }
                                break;
                            case "4":
                                Tiempos_04.add(tiempo);
                                if (Tiempos_04.size()>=dataSnapshot.getChildrenCount()){
                                    CALCULAR(Tiempos_04, "4");
                                }
                                break;
                        }
                    }
                    pdvalidando.dismiss();
                } else {
                    //Toast.makeText(Estadistico.this, "No se tienen registros de la entrada: "+in, Toast.LENGTH_LONG).show();
                    pdvalidando.dismiss();
                    snack = snack + nombre;
                    Snackbar.make(
                            findViewById(R.id.layout_estadistico), snack, Snackbar.LENGTH_LONG).
                            show();
                    switch (in){
                        case "1":
                            btn01.setVisibility(View.GONE);
                            break;
                        case "2":
                            btn02.setVisibility(View.GONE);;
                            break;
                        case "3":
                            btn03.setVisibility(View.GONE);;
                            break;
                        case "4":
                            btn04.setVisibility(View.GONE);;
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show();
                pdvalidando.dismiss();
            }
        });
    }

    private void CALCULAR(final ArrayList<String> tiempos, final String in) {
        Collections.sort(tiempos);
        String path = ("in"+in+"/estado"); myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long estado = (long) snapshot.getValue();
                    ON_OFF (tiempos, in, estado);
                    HORA_HORA(tiempos, estado, Integer.parseInt(in));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show(); pdvalidando.dismiss();}
        });
    }

    public void CALCULOS(View view) {

        // COMENSAR Y PROGRESS BAR...
        Ons.clear(); Offs.clear();
        for (int x = 0; x<=3; x++){
            Ons.add(x,0.0);
            Offs.add(x,0.0);
        }
        Collections.sort(Tiempos_01); Collections.sort(Tiempos_02); Collections.sort(Tiempos_03); Collections.sort(Tiempos_04);

        if (Tiempos_01.size()>0){
            String path = ("in1/estado"); myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long estado = (long) snapshot.getValue();
                    ON_OFF (Tiempos_01, "1", estado);
                    Contador01.clear();
                    HORA_HORA(Tiempos_01, estado, 1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show(); }
            });
        }
        // Senal 2
        if (Tiempos_02.size()>0){
            String path = ("in2/estado"); myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long estado = (long) snapshot.getValue();
                    ON_OFF (Tiempos_02, "2", estado);
                    Contador02.clear();
                    HORA_HORA(Tiempos_02, estado, 2);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show(); }
            });
        }
        // Senal 3
        if (Tiempos_03.size()>0){
            String path = ("in3/estado"); myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long estado = (long) snapshot.getValue();
                    ON_OFF (Tiempos_03, "3", estado);
                    Contador03.clear();
                    HORA_HORA(Tiempos_03, estado, 3);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show(); }
            });
        }
        // Senal 4
        if (Tiempos_04.size()>0){
            String path = ("in4/estado"); myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long estado = (long) snapshot.getValue();
                    ON_OFF (Tiempos_04, "4", estado);
                    Contador04.clear();
                    HORA_HORA(Tiempos_04, estado, 4);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show(); }
            });
        }
    }

    private void HORA_HORA(ArrayList<String> tempos, long estado, int in) {
        // Definir la cantidad de horas (o dias)
        //int Z = 24;

        int start;
        if (estado==0){ start = 2;}
        else { start = 1;}

        ArrayList<Integer> Contadores = new ArrayList<>();
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long t1 = ahora.getTime();
        for (int x=0; x <= lapso; x++){
            //Contadores.clear();
            long t2 = t1 - milis; int count = 0;
            for (int y = tempos.size()-start; y >= 0 ; y=y-2){
                long pos = Long.parseLong(tempos.get(y));
                if (pos > t2 & pos < t1){
                    count++;
                }
            }
            Contadores.add(count);
            t1 = t2;
            switch (in){
                case 1:
                    Contador01.add(count);
                    break;
                case 2:
                    Contador02.add(count);
                    break;
                case 3:
                    Contador03.add(count);
                    break;
                case 4:
                    Contador04.add(count);
                    break;
            }
        }
        System.out.println(Contadores);
    }

    private void ON_OFF(ArrayList<String> tempos, String i, long estado) {
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long t1 = ahora.getTime();
        System.out.println("Hora actual: "+ t1);
        long on=0, off=0;
        int ii = Integer.parseInt(i)-1;

        for (int x=tempos.size()-1; x>=0 ; x--){
            long t2 = Long.parseLong(tempos.get(x)); System.out.println(x+" Tiempo: "+t2);
            if (estado==0){
                off = off + (t1 -t2);
                estado = 1;
            }else {
                on = on + (t1 -t2);
                estado = 0;
            }
            t1 = t2;
            if (x==0){
                double Horas_on = (double)on/3600000; double Horas_off = (double)off/3600000;
                System.out.println("Señal #"+i+" Tiempo On: "+ Horas_on); System.out.println(" Tiempo Off: "+ Horas_off);
                Ons.set(ii, Horas_on); Offs.set(ii, Horas_off);
                //Ons.add(Horas_on); Offs.add(Horas_off);
                }
            }
    }

    @SuppressLint("ResourceAsColor")
    public void IN01(View view) {
        ArrayList<PieEntry> valores = new ArrayList<>();
        final double A = Ons.get(0);
        valores.add(new PieEntry((float) A, "ON"));
        double B = Offs.get(0);
        valores.add(new PieEntry((float) B, "OFF"));

        // pidiendo los KwH si los tiene:
        String path = ("in1/kwh"); myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double kwh4 = (double)snapshot.getValue()*A;
                    String consumo = String.format("%.2f", kwh4)+" [kW]";
                    tvkW.setText(consumo);
                } else {
                    tvkW.setText("sin dato");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        PieDataSet dataSet = new PieDataSet(valores, "Porcentaje ON/OFF Entrada 01:");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.invalidate();

        relativeLayout1.setVisibility(View.VISIBLE);

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long timer = (ahora.getTime()-Long.parseLong(Tiempos_01.get(0)))/(24*3600000);

        //POBLANDO LA INFORMACION GENERAL:
        CharSequence charSequence1 = timer+" dias";
        tvDias.setText(charSequence1);
        CharSequence charSequence2 = Tiempos_01.size()/2+"";
        tvCiclos.setText(charSequence2);
        CharSequence charSequence3 = String.format("%.2f", A)+" [hr]";
        tvON.setText(charSequence3);

/*        CharSequence charSequence = "Detalle de la IN_01:\n\nHoras en ON:\n"+ String.format("%.2f", A) + "\nHoras en OFF:\n"+ String.format("%.2f", B) +
                "\nCiclos de vida:\n"+Tiempos_01.size()/2+"\nVida util en dias:\n"+timer;
        infoIn.setText(charSequence);*/

        GRAF_BARRAS(Contador01);
    }
    @SuppressLint("ResourceAsColor")
    public void IN02(View view) {
        ArrayList<PieEntry> valores = new ArrayList<>();
        final double A = Ons.get(1);
        valores.add(new PieEntry((float) A, "ON"));
        double B = Offs.get(1);
        valores.add(new PieEntry((float) B, "OFF"));
        // pidiendo los KwH si los tiene:
        String path = ("in2/kwh"); myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double kwh4 = (double)snapshot.getValue()*A;
                    String consumo = String.format("%.2f", kwh4)+" [kW]";
                    tvkW.setText(consumo);
                } else {
                    tvkW.setText("sin dato");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        PieDataSet dataSet = new PieDataSet(valores, "Porcentaje ON/OFF Entrada 02:");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.invalidate();

        relativeLayout1.setVisibility(View.VISIBLE);

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long timer = (ahora.getTime()-Long.parseLong(Tiempos_02.get(0)))/(24*3600000);

        //POBLANDO LA INFORMACION GENERAL:
        CharSequence charSequence1 = timer+" dias";
        tvDias.setText(charSequence1);
        CharSequence charSequence2 = Tiempos_02.size()/2+"";
        tvCiclos.setText(charSequence2);
        CharSequence charSequence3 = String.format("%.2f", A)+" [hr]";
        tvON.setText(charSequence3);

/*        CharSequence charSequence = "Detalle de la IN_01:\n\nHoras en ON:\n"+ String.format("%.2f", A) + "\nHoras en OFF:\n"+ String.format("%.2f", B) +
                "\nCiclos de vida:\n"+Tiempos_01.size()/2+"\nVida util en dias:\n"+timer;
        infoIn.setText(charSequence);*/

        GRAF_BARRAS(Contador02);
    }
    public void IN03(View view) {
        ArrayList<PieEntry> valores = new ArrayList<>();
        final double A = Ons.get(2);
        valores.add(new PieEntry((float) A, "ON"));
        double B = Offs.get(2);
        valores.add(new PieEntry((float) B, "OFF"));

        // ARRANCAR PROGRESS DIAG.

        // pidiendo los KwH si los tiene:
        String path = ("in3/kwh"); myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double kwh4 = (double)snapshot.getValue()*A;
                    String consumo = String.format("%.2f", kwh4)+" [kW]";
                    tvkW.setText(consumo);
                } else {
                    tvkW.setText("sin dato");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        PieDataSet dataSet = new PieDataSet(valores, "Porcentaje ON/OFF Entrada 03:");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.invalidate();

        relativeLayout1.setVisibility(View.VISIBLE);

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long timer = (ahora.getTime()-Long.parseLong(Tiempos_03.get(0)))/(24*3600000);

        //POBLANDO LA INFORMACION GENERAL:
        CharSequence charSequence1 = timer+" dias";
        tvDias.setText(charSequence1);
        CharSequence charSequence2 = Tiempos_03.size()/2+"";
        tvCiclos.setText(charSequence2);
        CharSequence charSequence3 = String.format("%.2f", A)+" [hr]";
        tvON.setText(charSequence3);


/*        CharSequence charSequence = "Detalle de la IN_01:\n\nHoras en ON:\n"+ String.format("%.2f", A) + "\nHoras en OFF:\n"+ String.format("%.2f", B) +
                "\nCiclos de vida:\n"+Tiempos_01.size()/2+"\nVida util en dias:\n"+timer;
        infoIn.setText(charSequence);*/

        GRAF_BARRAS(Contador03);
    }
    public void IN04(View view) {
        ArrayList<PieEntry> valores = new ArrayList<>();
        final double A = Ons.get(3);
        String consumo;
        valores.add(new PieEntry((float) A, "ON"));
        double B = Offs.get(3);
        valores.add(new PieEntry((float) B, "OFF"));

        // ARRANCAR PROGRESS DIAG.

        // pidiendo los KwH si los tiene:
        String path = ("in4/kwh"); myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double kwh4 = (double)snapshot.getValue()*A;
                    String consumo = String.format("%.2f", kwh4)+" [kW]";
                    tvkW.setText(consumo);
                } else {
                    tvkW.setText("sin dato");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Estadistico.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        PieDataSet dataSet = new PieDataSet(valores, "Porcentaje ON/OFF Entrada 04:");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.invalidate();

        relativeLayout1.setVisibility(View.VISIBLE);

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        long timer = (ahora.getTime()-Long.parseLong(Tiempos_04.get(0)))/(24*3600000);

        //POBLANDO LA INFORMACION GENERAL:
        CharSequence charSequence1 = timer+" dias";
        tvDias.setText(charSequence1);
        CharSequence charSequence2 = Tiempos_04.size()/2+"";
        tvCiclos.setText(charSequence2);
        CharSequence charSequence3 = String.format("%.2f", A)+" [hr]";
        tvON.setText(charSequence3);

/*        CharSequence charSequence = "Detalle de la IN_01:\n\nHoras en ON:\n"+ String.format("%.2f", A) + "\nHoras en OFF:\n"+ String.format("%.2f", B) +
                "\nCiclos de vida:\n"+Tiempos_01.size()/2+"\nVida util en dias:\n"+timer;
        infoIn.setText(charSequence);*/

        GRAF_BARRAS(Contador04);
    }

    private void GRAF_BARRAS(ArrayList<Integer> contadores) {

        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=1; i<contadores.size();i++){
            values.add(new BarEntry(i, (float)contadores.get(i-1)));
        }
        System.out.println(values);

        BarDataSet set1;
        if (barChart.getData() != null &&  barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);  set1.setLabel(label);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, label);

            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
            barChart.setFitBars(true);
        }
        barChart.invalidate();
        barChart.setVisibility(View.VISIBLE);
    }

}
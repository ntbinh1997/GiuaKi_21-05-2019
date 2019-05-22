package com.example.giuaki_final;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.giuaki_final.adapter.ChamCongAdapter;
import com.example.giuaki_final.adapter.PhanXuongAdapter;
import com.example.giuaki_final.dao.ChamCongDAO;
import com.example.giuaki_final.dao.CongNhanDAO;
import com.example.giuaki_final.data.DBManager;
import com.example.giuaki_final.model.ChamCong;
import com.example.giuaki_final.model.CongNhan;
import com.example.giuaki_final.model.PhanXuong;

import java.util.ArrayList;
import java.util.List;

public class ChamCongActivity extends AppCompatActivity {
    private EditText edtNgay;
    private Spinner spnMaCN;
    private EditText edtSoGioLam;
    private Button btnThem;
    private Button btnCapNhat;
    private Button btnThoat;
    private boolean statusAdd = true;

    private ListView lvChamCong;
    private DBManager dbManager;
    private ChamCongAdapter chamCongAdapter;
    private ChamCongDAO chamCongDAO;

    private List<ChamCong> danhSachCC;
    private CongNhanDAO congNhanDAO;

    private int maCN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamcong);
        dbManager = new DBManager(this);
        initWidget();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý chấm công");
        congNhanDAO = new CongNhanDAO(dbManager);
        chamCongDAO = new ChamCongDAO(dbManager);
        danhSachCC = chamCongDAO.layDSCC();

        List<Integer> listSpn = congNhanDAO.layMaCN();
        List<String> listSpinner = new ArrayList<>(listSpn.size());
        for (Integer myInt : listSpn) {
            listSpinner.add(String.valueOf(myInt));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnMaCN.setAdapter(adapter);
        spnMaCN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maCN = Integer.parseInt(spnMaCN.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setAdapter();
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChamCong chamCong = taoChamCong(maCN);
                if (chamCong != null) {
                    chamCongDAO.themChamCong(chamCong);
                }
                capNhatDSCC();
                setAdapter();
                createNew();
            }
        });

        edtNgay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNull();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkNull();
            }
        });

        edtSoGioLam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNull();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkNull();
            }
        });

        lvChamCong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ChamCong chamCong = danhSachCC.get(position);
                edtNgay.setText(String.valueOf(chamCong.getmNgay()));
                edtSoGioLam.setText(chamCong.getmSoGioLam()+"");
                btnThem.setEnabled(false);
                btnCapNhat.setEnabled(true);
                btnThoat.setEnabled(true);
                statusAdd=false;
            }
        });

        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChamCong chamCong = new ChamCong();
                chamCong.setmSoGioLam(Integer.parseInt(String.valueOf(edtSoGioLam.getText())));
                chamCong.setmMaCN(maCN);
                chamCong.setmNgay(edtNgay.getText()+"");
                int result = chamCongDAO.capNhatCC(chamCong);
                if(result>0){
                    capNhatDSCC();
                }
                createNew();
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNew();
            }
        });

        lvChamCong.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChamCongActivity.this);
                builder.setCancelable(true);
                builder.setTitle("CẢNH BÁO !!!");
                builder.setMessage("Bạn có chắc muốn xóa sản phẩm này !");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.out.println("xóa thất bại");
                        dialog.cancel();
                    }

                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChamCong chamCong = danhSachCC.get(position);
                        int result = chamCongDAO.xoaCC(chamCong.getmNgay(), chamCong.getmMaCN());
                        if(result>0){
                            Toast.makeText(ChamCongActivity.this, "Delete successfuly", Toast.LENGTH_SHORT).show();
                            capNhatDSCC();
                        }else{
                            Toast.makeText(ChamCongActivity.this, "Delete fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });

    }



    private void initWidget() {
        edtNgay = findViewById(R.id.edt_ngay);
        spnMaCN = findViewById(R.id.spn_macn);
        edtSoGioLam = findViewById(R.id.edt_sogiolam);
        btnThem = findViewById(R.id.btn_them);
        btnCapNhat = findViewById(R.id.btn_capnhat);
        lvChamCong = findViewById(R.id.lv_chamcong);
        btnThoat = findViewById(R.id.btn_thoat);
    }

    private ChamCong taoChamCong(int maCN) {
        String ngay = edtNgay.getText().toString();
        int soGioLam = Integer.parseInt(edtSoGioLam.getText().toString());
        ChamCong chamCong = new ChamCong(ngay, maCN, soGioLam);
        return chamCong;
    }

    private void setAdapter() {
        if (chamCongAdapter == null) {
            chamCongAdapter = new ChamCongAdapter(this, R.layout.item_chamcong_layout, danhSachCC);
            lvChamCong.setAdapter(chamCongAdapter);
        }else{
            chamCongAdapter.notifyDataSetChanged();
            lvChamCong.setSelection(chamCongAdapter.getCount()-1);
        }
    }

    public void capNhatDSCC(){
        danhSachCC.clear();
        danhSachCC.addAll(chamCongDAO.layDSCC());
        if(chamCongAdapter!= null){
            chamCongAdapter.notifyDataSetChanged();
        }
    }

    public void checkNull(){
        if (edtNgay.getText().length() != 0 && edtSoGioLam.getText().length() !=0 ){
            btnThem.setEnabled(statusAdd);
            btnCapNhat.setEnabled(!statusAdd);
        }
    }
    public void createNew(){
        edtNgay.setText("");
        edtSoGioLam.setText("");
        btnCapNhat.setEnabled(false);
        btnThem.setEnabled(false);
        btnThoat.setEnabled(false);
    }
}

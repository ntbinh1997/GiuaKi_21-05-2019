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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.giuaki_final.adapter.CongNhanAdapter;
import com.example.giuaki_final.dao.CongNhanDAO;
import com.example.giuaki_final.dao.PhanXuongDAO;
import com.example.giuaki_final.data.DBManager;
import com.example.giuaki_final.model.CongNhan;
import com.example.giuaki_final.model.PhanXuong;

import java.util.ArrayList;
import java.util.List;

public class CongNhanActivity extends AppCompatActivity {
    private EditText edtMaCN;
    private EditText edtHo;
    private EditText edtTen;
    //private EditText edtPhai;
    private EditText edtNamSinh;
    private EditText edtNgayNV;
    private EditText edtLuongCB;
    private Spinner spnMaPX;
    private Button btnThem;
    private Button btnCapNhat;
    private ListView lvCongNhan;
    private DBManager dbManager;
    private Button btnThoat;
    private CongNhanAdapter congNhanAdapter;
    private CongNhanDAO congNhanDAO;
    private List<CongNhan> danhSachCN;
    private PhanXuongDAO phanXuongDAO;
    private RadioGroup radioSex;
    private boolean statusAdd = true;

    private int maPX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congnhan);
        dbManager = new DBManager(this);
        initWidget();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý công nhân");
        congNhanDAO = new CongNhanDAO(dbManager);
        phanXuongDAO = new PhanXuongDAO(dbManager);
        danhSachCN = congNhanDAO.layDSCN();

        List<Integer> listSpn = phanXuongDAO.layMaPX();
        List<String> listSpinner = new ArrayList<>(listSpn.size());
        for (Integer myInt : listSpn) {
            listSpinner.add(String.valueOf(myInt));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnMaPX.setAdapter(adapter);
        spnMaPX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maPX = Integer.parseInt(spnMaPX.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setAdapter();
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CongNhan congNhan = taoCongNhan(maPX);
                if (congNhan != null) {
                    congNhanDAO.themCongNhan(congNhan);
                }
                capNhatDSCN();
                setAdapter();
                createStatus();
            }
        });
        lvCongNhan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CongNhan congNhan = danhSachCN.get(position);
                edtMaCN.setText(String.valueOf(congNhan.getmMaCN()));
                edtHo.setText(congNhan.getmHo());
                edtTen.setText(congNhan.getmTen());
                radioSex.check(congNhan.getmPhai());
                edtNamSinh.setText(congNhan.getmNamSinh()+"");
                edtNgayNV.setText(congNhan.getmNgayNV());
                edtLuongCB.setText(congNhan.getmLuongCB()+"");
                btnThem.setEnabled(false);
                btnCapNhat.setEnabled(true);
                btnThoat.setEnabled(true);
                statusAdd=false;
            }
        });
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStatus();
            }
        });
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CongNhan congNhan = new CongNhan();
                congNhan.setmMaCN(Integer.parseInt(String.valueOf(edtMaCN.getText())));
                congNhan.setmHo(edtHo.getText()+"");
                congNhan.setmTen(edtTen.getText()+"");
                congNhan.setmPhai(radioSex.getCheckedRadioButtonId());
                congNhan.setmNamSinh(Integer.parseInt(edtNamSinh.getText().toString()));
                congNhan.setmNgayNV(edtNgayNV.getText()+"");
                congNhan.setmLuongCB(Integer.parseInt(edtLuongCB.getText().toString()));
                congNhan.setmMaPX(maPX);

                int result = congNhanDAO.capNhatCN(congNhan);
                if(result>0){
                    capNhatDSCN();
                }
                createStatus();
            }
        });

        edtHo.addTextChangedListener(new TextWatcher() {
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

        edtTen.addTextChangedListener(new TextWatcher() {
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


        edtNamSinh.addTextChangedListener(new TextWatcher() {
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


        edtLuongCB.addTextChangedListener(new TextWatcher() {
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


        edtNgayNV.addTextChangedListener(new TextWatcher() {
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


        lvCongNhan.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CongNhanActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Alert dialog");
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
                        CongNhan congNhan = danhSachCN.get(position);
                        int result = congNhanDAO.xoaCN(congNhan.getmMaCN());
                        if(result>0){
                            Toast.makeText(CongNhanActivity.this, "Delete successfuly", Toast.LENGTH_SHORT).show();
                            capNhatDSCN();
                        }else{
                            Toast.makeText(CongNhanActivity.this, "Delete fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }



    private void initWidget(){
        edtMaCN = findViewById(R.id.edt_macn);
        edtHo = findViewById(R.id.edt_ho);
        edtTen = findViewById(R.id.edt_ten);
        //edtPhai = findViewById(R.id.edt_phai);
        edtNamSinh = findViewById(R.id.edt_namsinh);
        edtNgayNV = findViewById(R.id.edt_ngaynv);
        edtLuongCB = findViewById(R.id.edt_luongcb);
        spnMaPX = findViewById(R.id.spn_mapx);
        btnThem = findViewById(R.id.btn_them);
        btnCapNhat = findViewById(R.id.btn_capnhat);
        lvCongNhan = findViewById(R.id.lv_congnhan);
        radioSex = findViewById(R.id.radioSex);
        btnThoat = findViewById(R.id.btn_thoat);
    }
    private void setAdapter() {
        if (congNhanAdapter == null) {
            congNhanAdapter = new CongNhanAdapter(this, R.layout.item_congnhan_layout, danhSachCN);
            lvCongNhan.setAdapter(congNhanAdapter);
        }else{
            congNhanAdapter.notifyDataSetChanged();
            lvCongNhan.setSelection(congNhanAdapter.getCount()-1);
        }
    }

    private CongNhan taoCongNhan(int maPX) {
        String ho = edtHo.getText().toString();
        String ten = edtTen.getText().toString();
        int phai = radioSex.getCheckedRadioButtonId();
        int namSinh = Integer.parseInt(edtNamSinh.getText().toString());
        String ngayNV = edtNgayNV.getText().toString();
        int luongCB = Integer.parseInt(edtLuongCB.getText().toString());
        CongNhan congNhan = new CongNhan(ho, ten, phai, namSinh, ngayNV, luongCB, maPX);
        return congNhan;
    }
    public void capNhatDSCN(){
        danhSachCN.clear();
        danhSachCN.addAll(congNhanDAO.layDSCN());
        if(congNhanAdapter!= null){
            congNhanAdapter.notifyDataSetChanged();
        }
    }
    public void checkNull(){
        if(edtHo.getText().length() != 0 && edtTen.getText().length() != 0&& edtLuongCB.getText().length() != 0&& edtNamSinh.getText().length() != 0&& edtNgayNV.getText().length() != 0){
            btnThem.setEnabled(statusAdd);
            btnThoat.setEnabled(!statusAdd);
        }else{
            btnThem.setEnabled(false);
            btnThoat.setEnabled(false);
        }
    }
    public void createStatus (){
        edtTen.setText("");
        edtHo.setText("");
        edtNgayNV.setText("");
        edtLuongCB.setText("");
        edtMaCN.setText("");
        edtNamSinh.setText("");
        statusAdd=!statusAdd;
        btnThoat.setEnabled(false);
        btnThem.setEnabled(false);
        btnCapNhat.setEnabled(false);
    }
}

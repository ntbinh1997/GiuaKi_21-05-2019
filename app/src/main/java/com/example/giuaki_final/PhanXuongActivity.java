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

import com.example.giuaki_final.adapter.PhanXuongAdapter;
import com.example.giuaki_final.dao.PhanXuongDAO;
import com.example.giuaki_final.dao.SanPhamDAO;
import com.example.giuaki_final.data.DBManager;
import com.example.giuaki_final.model.PhanXuong;

import java.util.ArrayList;
import java.util.List;

public class PhanXuongActivity extends AppCompatActivity {
    private EditText edtMaPX;
    private EditText edtTenPX;
    private Spinner spnMaSP;
    private Button btnLuu;
    private Button btnCapNhat;
    private Button btnThoat;
    private ListView lvPhanXuong;
    private DBManager dbManager;
    private PhanXuongAdapter phanXuongAdapter;
    private PhanXuongDAO phanXuongDAO;
    private List<PhanXuong> danhSachPX;
    private SanPhamDAO sanPhamDAO;
    private boolean statusAd = true;

    private int maSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phanxuong);
        dbManager = new DBManager(this);
        initWidget();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý phân xưởng");
        sanPhamDAO = new SanPhamDAO(dbManager);
        phanXuongDAO = new PhanXuongDAO(dbManager);
        danhSachPX = phanXuongDAO.layDSPX();
        List<Integer> listSpn = sanPhamDAO.layMaSP();
        List<String> listSpinner = new ArrayList<>(listSpn.size());
        for (Integer myInt : listSpn) {
            listSpinner.add(String.valueOf(myInt));
        }
        btnLuu.setEnabled(false);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnMaSP.setAdapter(adapter);
        spnMaSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maSP = Integer.parseInt(spnMaSP.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtTenPX.addTextChangedListener(new TextWatcher() {
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

        setAdapter();
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhanXuong phanXuong = taoPhanXuong(maSP);
                phanXuongDAO.themPhanXuong(phanXuong);
                capNhatDSPX();
                setAdapter();
                createNew();
            }
        });

        lvPhanXuong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PhanXuong phanXuong = danhSachPX.get(position);
                System.out.println(phanXuong);
                edtMaPX.setText(String.valueOf(phanXuong.getmMaPX()));
                edtTenPX.setText(phanXuong.getmTenPX());
                btnLuu.setEnabled(false);
                btnCapNhat.setEnabled(true);
                btnThoat.setEnabled(true);
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNew();
            }
        });

        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhanXuong phanXuong = new PhanXuong();
                phanXuong.setmMaPX(Integer.parseInt(String.valueOf(edtMaPX.getText())));
                phanXuong.setmTenPX(edtTenPX.getText()+"");
                phanXuong.setmMaSP(maSP);
                int result = phanXuongDAO.capNhatPX(phanXuong);
                if(result>0){
                    capNhatDSPX();
                }
                createNew();
            }
        });
        lvPhanXuong.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(PhanXuongActivity.this);
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
                        PhanXuong phanXuong = danhSachPX.get(position);
                        int result = phanXuongDAO.xoaPX(phanXuong.getmMaPX());
                        if(result>0){
                            Toast.makeText(PhanXuongActivity.this, "Delete successfuly", Toast.LENGTH_SHORT).show();
                            capNhatDSPX();
                        }else{
                            Toast.makeText(PhanXuongActivity.this, "Delete fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    private void initWidget() {
        edtMaPX = findViewById(R.id.edt_mapx);
        edtTenPX = findViewById(R.id.edt_tenpx);
        spnMaSP = findViewById(R.id.spn_masp);
        btnLuu = findViewById(R.id.btn_luu);
        btnCapNhat = findViewById(R.id.btn_capnhat);
        lvPhanXuong = findViewById(R.id.lv_phanxuong);
        btnThoat = findViewById(R.id.btn_thoat);
    }

    private PhanXuong taoPhanXuong(int maSP) {
        String tenPX = edtTenPX.getText().toString();
        PhanXuong phanXuong = new PhanXuong(tenPX, maSP);
        return phanXuong;
    }

    private void setAdapter() {
        if (phanXuongAdapter == null) {
            phanXuongAdapter = new PhanXuongAdapter(this, R.layout.item_phanxuong_layout, danhSachPX);
            lvPhanXuong.setAdapter(phanXuongAdapter);
        }else{
            phanXuongAdapter.notifyDataSetChanged();
            lvPhanXuong.setSelection(phanXuongAdapter.getCount()-1);
        }
    }

    public void capNhatDSPX(){
        danhSachPX.clear();
        danhSachPX.addAll(phanXuongDAO.layDSPX());
        if(phanXuongAdapter!= null){
            phanXuongAdapter.notifyDataSetChanged();
        }
    }
    public void checkNull (){
        if(edtTenPX.getText().length() != 0) {
            btnCapNhat.setEnabled(!statusAd);
            btnLuu.setEnabled(statusAd);
        }
    }
    public void createNew(){
        statusAd = true;
        btnThoat.setEnabled(false);
        btnCapNhat.setEnabled(false);
        btnLuu.setEnabled(false);
        edtTenPX.setText("");
        edtMaPX.setText("");
    }
}

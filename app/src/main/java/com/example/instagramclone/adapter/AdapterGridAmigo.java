package com.example.instagramclone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AdapterGridAmigo  extends ArrayAdapter<String> {
    private Context c;
    private int layoutResource;
    private List<String> urlFotosLista;


    public AdapterGridAmigo(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.c = context;
        this.layoutResource = resource;
        this.urlFotosLista = objects;
    }

    public class ViewHolderGrid{
        ImageView ivPost;
        ProgressBar progressBarPost;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolderGrid viewHolderGrid;

        if (convertView == null){
            viewHolderGrid = new ViewHolderGrid();
            LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolderGrid.progressBarPost = convertView.findViewById(R.id.progressGrid);
            viewHolderGrid.ivPost = convertView.findViewById(R.id.ivGrid);
            convertView.setTag(viewHolderGrid);

        }else {
            viewHolderGrid = (ViewHolderGrid) convertView.getTag();
        }

        // Recupera dados da Imagem
        String urlImagens = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlImagens, viewHolderGrid.ivPost,
                new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // Quando começa o processo de Carregamento
                viewHolderGrid.progressBarPost.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // Quando ocorre um erro ao carregar
                // Erro ná biblioteca
                Log.i("ErroLoading", "Ocorreu um erro na biblioteca");
                viewHolderGrid.progressBarPost.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Quando o Carregamento foi completado
                viewHolderGrid.progressBarPost.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                // Quando o carregamento for cancelado
                // Erro no código
                Log.i("ErroLoading", "Seu código possui um erro");
                viewHolderGrid.progressBarPost.setVisibility(View.GONE);
            }
        });

        return convertView;
    }
}

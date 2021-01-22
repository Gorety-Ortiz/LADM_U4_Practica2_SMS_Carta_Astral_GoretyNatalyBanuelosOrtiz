package mx.tecnm.tepic.ladm_u4_practica2_sms_carta_astral

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button2.setOnClickListener {
            MostrarRegistros()
        }
        button.setOnClickListener {
            GuardarBD()
        }

        //PERMISO PARA RECIBIR LOS MENSAJES ENTRANTES
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_SMS),siPermisoLectura)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            //SOLICITUD DE PERMISO, EN CASO DE QUE ESTE NO ESTE PREVIAMENTE OTORGADO
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS), siPermiso
            )
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==siPermiso){
            //al mandar el permiso de forma imediata llama el metodo

        }
        //AQUI SE EVALUA SI SE OTORGO EL PERMISO
        if(requestCode==siPermisoReceiver){
            //AQUI SE MANDA EL MENSAJE DE QUE SE OTORGO EL PERMISO

        }
        if(requestCode==siPermisoLectura){

        }
    }

    fun mensaje(mensaje:String){
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .show()

    }

    //GUARDAR NUEVOS REGISTROS EN LA BASE DE DATOS
    fun GuardarBD(){
        var zigno = txtZigno.text.toString()
        var carta = txtCartaAstral.text.toString()

        try{
            var baseDatos = BaseDatos (this, "BDAstral", null,5)

            var insertar = baseDatos.writableDatabase
            var SQL = "INSERT INTO CARTA_ASRTAL VALUES('${carta}','${zigno}')"

            insertar.execSQL(SQL)
            baseDatos.close()

            mensaje("Se insertó correctamente")
            txtCartaAstral.setText("")
            txtZigno.setText("")


        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }
    }

    //HACER SELECT EN LA TABLA PARA MOSTRAR REGISTROS
    fun MostrarRegistros(){
        try {
            val cursor = BaseDatos(this, "BDAstral",null, 5)
                .readableDatabase
                .rawQuery("SELECT * FROM CARTA_ASRTAL",null)
            var resultado = ""

            if(cursor!!.moveToFirst()){
                do{
                    resultado += cursor.getString(1)+":\n"+cursor.getString(0)+"\n- - - - - - - - - - - - - - - - -\n"

                }while (cursor.moveToNext())

            }else{
                resultado = "No hay Cartas Astrales"
            }
            textView2.setText(resultado)

        }catch(error:SQLiteException) {
            mensaje(error.message.toString())
        }
    }

}
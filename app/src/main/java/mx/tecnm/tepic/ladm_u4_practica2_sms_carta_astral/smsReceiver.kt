package mx.tecnm.tepic.ladm_u4_practica2_sms_carta_astral

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.SQLException


//RECEIVER = un evento u oyente de android que permite la lectura de eventos del sistema operativo

class smsReceiver : BroadcastReceiver(){
    var mensaje2 = ""
    override fun onReceive(context: Context?, intent: Intent) {
        //obtener parametros de envio de algo hacia un activity
        val extras = intent.extras

//contiene los mensajes que vienen, "pdus" es el nombre de los extras
        if(extras != null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                val formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()
                var mensaje = contenidoSMS.split(" ")
                Toast.makeText(context, "Entró contenido ${contenidoSMS}",Toast.LENGTH_LONG).show()
                if (mensaje.size != 2){
                    SmsManager.getDefault().sendTextMessage(celularOrigen,null,"Por favor envie: CARTA_ASTRAL seguido de un espacio, y su zigno zodiacal: CARTA_ASTRAL VIRGO",null,null)
                }
                else if (!mensaje[0].equals("CARTA_ASTRAL"))
                    {
                    SmsManager.getDefault().sendTextMessage(celularOrigen,null,"Por favor siga la sintaxis en mayusculas, ejemplo: CARTA_ASTRAL VIRGO",null,null)
                    }
                else
                {
                    //REALIZAR BUSQUEDA EN LA TABLA CARTA_ASTRAL
                    try {
                        val cursor = BaseDatos(context, "BDAstral", null, 5)
                            .readableDatabase
                            .rawQuery("SELECT CARTA FROM CARTA_ASRTAL WHERE ZIGNO = '${mensaje[1]}'",null)
                        if(cursor.moveToNext()){
                            mensaje2="Tu Carta Astral es: "+cursor.getString(0)
                            SmsManager.getDefault().sendTextMessage(
                                celularOrigen,null,mensaje2,null,null
                            )
                        }else{
                            SmsManager.getDefault().sendTextMessage(
                                celularOrigen, null, "no hay Carta Astral para ese zigno", null, null
                            )
                        }

                    }catch (err: SQLiteException){
                        Toast.makeText(context, err.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
}
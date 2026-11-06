package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private fun calculaDias(
        mesActual: Int,
        mesNacimiento: Int,
        diaActual: Int,
        diaNacimiento: Int,
        anhoActual: Int,
        anhoNacimiento: Int
    ): IntArray {
        var dias = 0
        val calculo = IntArray(2)
        var anhos = anhoActual - anhoNacimiento
        if (mesActual >= mesNacimiento) {
            for (i in mesNacimiento + 1..mesActual) dias += numeroDeDias(i, anhoActual)
            if (diaActual < diaNacimiento && mesActual == mesNacimiento) {
                anhos--
                dias = 365 + diaActual - diaNacimiento
            } else {
                dias += diaActual - diaNacimiento
            }
        } else {
            anhos--
            dias = diasRestantes(diaNacimiento, mesNacimiento, anhoActual - 1)
            val veces = 12 + mesActual - mesNacimiento - 1
            var mesVer = mesNacimiento + 1
            var anhoVer = anhoActual - 1
            for (i in 1..veces) {
                if (mesVer > 12) {
                    mesVer = 1
                    anhoVer++
                }
                dias += numeroDeDias(mesVer, anhoVer)
                mesVer++
            }
            dias += diaActual
        }
        calculo[0] = anhos
        calculo[1] = dias
        return calculo
    }

    private fun esBisiesto(anho: Int): Boolean {
        var bisiesto = false
        if (anho > 1582) {
            if (anho % 400 == 0 || anho % 4 == 0 && anho % 100 != 0) {
                bisiesto = true
            }
        } else {
            bisiesto = false
        }
        return bisiesto
    }

    private fun numeroDeDias(m: Int, a: Int): Int {
        return when (m) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            else -> if (esBisiesto(a)) {
                29
            } else {
                28
            }
        }
    }

    private fun diasRestantes(dia: Int, mes: Int, anho: Int): Int {
        return numeroDeDias(mes, anho) - dia
    }
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonFechaNac.setOnClickListener {
            val datepicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val resultado = calculaDias(
                        month + 1,
                        selectedMonth + 1,
                        day,
                        selectedDay,
                        year,
                        selectedYear
                    )

                    binding.calculoDias.text =
                        "Tienes ${resultado[0]} años y ${resultado[1]} días"
                    binding.naciste.text = "Naciste el ${selectedDay}/${selectedMonth+1}/${selectedYear}"
                },
                year,
                month,
                day
            ).show()
        }

        binding.fechaActual.text = getString(R.string.fechaActual, fecha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
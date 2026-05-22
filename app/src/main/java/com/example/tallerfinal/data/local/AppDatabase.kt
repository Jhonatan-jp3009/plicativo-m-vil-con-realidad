package com.example.tallerfinal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tallerfinal.data.local.dao.AppDao
import com.example.tallerfinal.data.local.entity.DrawingReferenceEntity
import com.example.tallerfinal.data.local.entity.TutorialProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal de la aplicación construida con SQLite a través de la librería Room.
 * Declara las tablas (entidades) del esquema y define el patrón de acceso a los DAOs.
 * Implementa el patrón de diseño Singleton para garantizar que haya una única conexión activa
 * y evitar bloqueos en disco ("Database is locked").
 */
@Database(
    entities = [DrawingReferenceEntity::class, TutorialProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto para obtener el DAO. Room genera la implementación de forma automática.
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia Singleton de la base de datos.
         * Utiliza bloqueo sincronizado para que dos hilos no puedan crear dos bases de datos al mismo tiempo.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taller_final_database"
                )
                // En desarrollo rápido, si cambia el esquema, se borra y recrea la base de datos de manera segura.
                .fallbackToDestructiveMigration()
                // Callback para poblar la base de datos con pasos iniciales del tutorial al crearse por primera vez.
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback que se ejecuta cuando Room inicializa la base de datos por primera vez.
         * Rellena las etapas iniciales de la HU 8.5 (Tutorial de Simplificación Geométrica)
         * para que estén listas sin necesidad de internet (HU 8.2).
         */
        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Poblar de forma asíncrona usando Coroutines en el pool de hilos de entrada/salida (IO)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = getDatabase(context).appDao()
                    
                    // Registramos los 4 pasos iniciales del tutorial anatómico simplificado
                    dao.insertOrUpdateProgress(
                        TutorialProgressEntity(1, "1. Canon Proporciones: Cabeza y Cuello (Ovoides)")
                    )
                    dao.insertOrUpdateProgress(
                        TutorialProgressEntity(2, "2. Estructura del Torso y Pelvis (Cajas y Cilindros)")
                    )
                    dao.insertOrUpdateProgress(
                        TutorialProgressEntity(3, "3. Extremidades e Articulaciones (Líneas y Esferas)")
                    )
                    dao.insertOrUpdateProgress(
                        TutorialProgressEntity(4, "4. Integración y Detalles Anatómicos Completos")
                    )
                }
            }
        }
    }
}

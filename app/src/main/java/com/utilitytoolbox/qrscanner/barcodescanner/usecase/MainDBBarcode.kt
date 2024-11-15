package com.utilitytoolbox.qrscanner.barcodescanner.usecase


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.utilitytoolbox.qrscanner.barcodescanner.model.MineBarCode
import com.utilitytoolbox.qrscanner.barcodescanner.model.schema
.BarcodeSchema
import com.google.zxing.BarcodeFormat
import io.reactivex.Completable
import io.reactivex.Single


class BarcodeDatabaseTypeConverter {

    @TypeConverter
    fun fromBarcodeFormat(barcodeFormat: BarcodeFormat): String {
        return barcodeFormat.name
    }

    @TypeConverter
    fun toBarcodeFormat(value: String): BarcodeFormat {
        return BarcodeFormat.valueOf(value)
    }

    @TypeConverter
    fun fromBarcodeSchema(barcodeSchema: BarcodeSchema): String {
        return barcodeSchema.name
    }

    @TypeConverter
    fun toBarcodeSchema(value: String): BarcodeSchema {
        return BarcodeSchema.valueOf(value)
    }
}


@Database(entities = [MineBarCode::class], version = 3, exportSchema = false)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}


@Dao
interface BarcodeDatabase {
    @Query("SELECT * FROM qr_codes ORDER BY datetime DESC")
    fun getAllCodes(): DataSource.Factory<Int, MineBarCode>

    @Query("SELECT * FROM qr_codes ORDER BY datetime DESC ")
    fun getAllHistory(): LiveData<MutableList<MineBarCode>>

    @Query("SELECT * FROM qr_codes WHERE isFavorite = 1 ORDER BY datetime DESC")
    fun getFavoritesHistory(): LiveData<MutableList<MineBarCode>>

    @Query("SELECT * FROM qr_codes WHERE isFavorite = 1 ORDER BY datetime DESC")
    fun getFavoritesCodes(): DataSource.Factory<Int, MineBarCode>

    @Query("SELECT * FROM qr_codes WHERE format = :format AND text = :text LIMIT 1")
    fun checkCodeIsAvailable(format: String, text: String): Single<List<MineBarCode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCode(MineBarCode: MineBarCode): Single<Long>

    @Query("DELETE FROM qr_codes WHERE id = :id")
    fun deleteCode(id: Long): Completable

    @Query("SELECT COUNT(*) > 0 FROM qr_codes WHERE formattedText = :formattedText")
    fun isFormattedTextExists(formattedText: String): Boolean

    @Query("DELETE FROM qr_codes")
    fun clearAll(): Completable
}

fun BarcodeDatabase.insertCode(MineBarCode: MineBarCode, doNotSaveDuplicates: Boolean): Single<Long> {
    return if (!doNotSaveDuplicates) {
        insertCodeIfNotExist(MineBarCode)
    } else {
        insertCode(MineBarCode)
    }
}

fun BarcodeDatabase.insertCodeIfNotExist(MineBarCode: MineBarCode): Single<Long> {
    return checkCodeIsAvailable(MineBarCode.format.name, MineBarCode.text)
        .flatMap { found ->
            if (found.isEmpty()) {
                insertCode(MineBarCode)
            } else {
                Single.just(found[0].id)
            }
        }
}

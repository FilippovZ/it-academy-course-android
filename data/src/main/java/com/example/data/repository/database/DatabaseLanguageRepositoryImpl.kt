package com.example.data.repository.database

import com.example.data.ext.convertLanguageDtoToEntity
import com.example.domain.dto.room.RoomLanguageOfOneCountryDto
import com.example.data.ext.convertLanguageEntityToDto
import com.example.data.room.DatabaseInfo
import com.example.data.room.LanguagesInfoEntity
import io.reactivex.rxjava3.core.Flowable

class DatabaseLanguageRepositoryImpl(private val db: DatabaseInfo) :
    com.example.domain.repository.DatabaseLanguageRepository {

//    override fun add(entity: LanguagesInfoEntity) = db.getLanguageInfoDAO().add(entity)

    override fun getAllInfo(): Flowable<List<RoomLanguageOfOneCountryDto>> =
        db.getLanguageInfoDAO().getAllInfo().map { it.convertLanguageEntityToDto() }

    override fun getLanguageByCountry(name: String): List<String> =
        db.getLanguageInfoDAO().getLanguageByCountry(name)

    override fun addAll(list: List<RoomLanguageOfOneCountryDto>) = db.getLanguageInfoDAO().addAll(list.convertLanguageDtoToEntity())

}
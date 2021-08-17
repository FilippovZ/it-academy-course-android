package com.repository.database

import com.example.course_android.dto.room.RoomCountryDescriptionItemDto
import com.example.course_android.room.CountryBaseInfoEntity
import com.example.course_android.room.DatabaseInfo
import com.example.course_android.utils.convertCountryEntityToDto
import io.reactivex.rxjava3.core.Flowable

class DatabaseCountryRepositoryImpl(private val db: DatabaseInfo) : DatabaseCountryRepository {
    override fun getAllInfo(): Flowable<List<RoomCountryDescriptionItemDto>> =
        db.getCountryInfoDAO().getAllInfo()
            .map { it.convertCountryEntityToDto() }

    override fun add(entity: CountryBaseInfoEntity) = db.getCountryInfoDAO().add(entity)

    override fun addAll(list: List<CountryBaseInfoEntity>) = db.getCountryInfoDAO().addAll(list)

}
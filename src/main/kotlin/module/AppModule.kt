package org.delcom.module

import org.delcom.repositories.IBagRepository
import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.BagRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.BagService
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository (TIDAK DIUBAH)
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service (TIDAK DIUBAH)
    single {
        PlantService(get())
    }

    // Profile Service (TIDAK DIUBAH)
    single {
        ProfileService()
    }

    // Bag Repository <-- TAMBAHAN BARU
    single<IBagRepository> {
        BagRepository()
    }

    // Bag Service <-- TAMBAHAN BARU
    single {
        BagService(get())
    }
}
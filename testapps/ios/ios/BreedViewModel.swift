//
//  BreedViewModel.swift
//  KMMBridgeKickStartIos
//
//  Created by Júlia Jakubcová on 03/10/2022.
//

import Foundation
import Combine
import allshared

@MainActor
class BreedViewModel : ObservableObject {
    
    private let repository: BreedsBreedRepository
    private let breedAnalytics: BreedAnalytics
    
    @Published
    var breeds: [BreedsBreed]?
    
    @Published
    var dataState: BreedsBreedDataRefreshState
    
    @Published
    var dataEvent: BreedsBreedDataEvent
    
    private var cancellables = [AnyCancellable]()
    
    init(repository: BreedsBreedRepository, breedAnalytics: BreedAnalytics) {
        self.repository = repository
        self.breedAnalytics = breedAnalytics
        self.dataState = repository.dataState.value // Sync access to the value, if you need it.
        self.dataEvent = BreedsBreedDataEvent.Initial.shared
    }
    
    @MainActor
    func activate() async {
        //        try? await self.repository.refreshBreedsIfStale()
        
        Task {
            for await breeds in self.repository.getBreeds() {
                self.breeds = breeds
            }
        }
        
        Task {
            for await event in self.repository.dataEvents {
                self.dataEvent = event
            }
        }
        
        for await state in self.repository.dataState {
            print("dataState: \(state)")
            self.dataState = state
        }
    }
    
    func onBreedFavorite(_ breed: BreedsBreed) {
        if let breedModel = breeds?.first(where: { $0.id == breed.id }) {
            Task{
                try? await repository.updateBreedFavorite(breed: breedModel)
            }
        } else {
            fatalError("Breed \(breed) not found.")
        }
    }
    
    func refresh() {
        Task{
            breedAnalytics.refreshingBreeds()
            try? await repository.refreshBreeds()
        }
    }
}

//
//  ContentView.swift
//  KMMBridgeKickStartIos
//
//  Created by Júlia Jakubcová on 30/09/2022.
//

import Combine
import SwiftUI
import allshared

struct BreedListScreen: View {
    
    @ObservedObject
    var viewModel: BreedViewModel
    
    let breedAnalytics: BreedAnalytics
    
    var body: some View {
        BreedListContent(
            breedAnalytics: breedAnalytics,
            breeds: viewModel.breeds,
            onBreedFavorite: { viewModel.onBreedFavorite($0) },
            refresh: { viewModel.refresh() },
            dataState: viewModel.dataState,
            dataEvent: viewModel.dataEvent
        ).task {
            await viewModel.activate()
        }
    }
}

struct BreedListContent: View {
    
    let breedAnalytics: BreedAnalytics
    var breeds: [BreedsBreed]?
    var onBreedFavorite: (BreedsBreed) -> Void
    var refresh: () -> Void
    var dataState: BreedsBreedDataRefreshState
    var dataEvent: BreedsBreedDataEvent
    
    var body: some View {
        ZStack {
            VStack {
                if let breeds = breeds {
                    List(breeds, id: \.id) { breed in
                        BreedRowView(breed: breed) {
                            onBreedFavorite(breed)
                            breedAnalytics.favoriteClicked(id: breed.id)
                        }
                    }.onAppear {
                        breedAnalytics.displayingBreeds(size: Int32(breeds.count))
                    }
                }
                
                switch onEnum(of: dataEvent){
                case .loading:
                    Text("Loading...")
                case .error(let result):
                    Text("Whoops - \(result.reason.name)")
                        .foregroundColor(.red)
                        .onAppear {
                            breedAnalytics.displayingError(message: result.reason)
                        }
                case .refreshedSuccess(_):
                    Text("Success")
                case .initial(_):
                    Text("")
                }
                
                switch onEnum(of: dataState){
                case .empty:
                    Button("Load Data") {
                        refresh()
                    }
                case .cached:
                    Button("Refresh") {
                        refresh()
                    }
                case .loading:
                    Button("Refresh") {
                        refresh()
                    }.disabled(true)
                }
            }
        }
    }
}

struct BreedRowView: View {
    var breed: BreedsBreed
    var onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack {
                Text(breed.name)
                    .padding(4.0)
                Spacer()
                Image(systemName: (!breed.favorite) ? "heart" : "heart.fill")
                    .padding(4.0)
            }
        }
    }
}

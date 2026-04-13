<?php

namespace Database\Seeders;

use App\Models\Food;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class FoodSeeder extends Seeder
{
    public function run(): void
    {
        $foods = [
            ['name' => 'Alma', 'calories_per_100g' => 52, 'protein_per_100g' => 0.3, 'carbs_per_100g' => 14, 'fat_per_100g' => 0.2],
            ['name' => 'Banán', 'calories_per_100g' => 89, 'protein_per_100g' => 1.1, 'carbs_per_100g' => 23, 'fat_per_100g' => 0.3],
            ['name' => 'Narancs', 'calories_per_100g' => 47, 'protein_per_100g' => 0.9, 'carbs_per_100g' => 12, 'fat_per_100g' => 0.1],
            ['name' => 'Eper', 'calories_per_100g' => 32, 'protein_per_100g' => 0.7, 'carbs_per_100g' => 7.7, 'fat_per_100g' => 0.3],
            ['name' => 'Szőlő', 'calories_per_100g' => 69, 'protein_per_100g' => 0.7, 'carbs_per_100g' => 18, 'fat_per_100g' => 0.2],

            ['name' => 'Paradicsom', 'calories_per_100g' => 18, 'protein_per_100g' => 0.9, 'carbs_per_100g' => 3.9, 'fat_per_100g' => 0.2],
            ['name' => 'Uborka', 'calories_per_100g' => 15, 'protein_per_100g' => 0.7, 'carbs_per_100g' => 3.6, 'fat_per_100g' => 0.1],
            ['name' => 'Paprika', 'calories_per_100g' => 31, 'protein_per_100g' => 1.0, 'carbs_per_100g' => 6.0, 'fat_per_100g' => 0.3],
            ['name' => 'Sárgarépa', 'calories_per_100g' => 41, 'protein_per_100g' => 0.9, 'carbs_per_100g' => 10, 'fat_per_100g' => 0.2],
            ['name' => 'Brokkoli', 'calories_per_100g' => 34, 'protein_per_100g' => 2.8, 'carbs_per_100g' => 7, 'fat_per_100g' => 0.4],

            ['name' => 'Csirkemell', 'calories_per_100g' => 165, 'protein_per_100g' => 31, 'carbs_per_100g' => 0, 'fat_per_100g' => 3.6],
            ['name' => 'Pulykamell', 'calories_per_100g' => 135, 'protein_per_100g' => 29, 'carbs_per_100g' => 0, 'fat_per_100g' => 1.0],
            ['name' => 'Marhahús', 'calories_per_100g' => 250, 'protein_per_100g' => 26, 'carbs_per_100g' => 0, 'fat_per_100g' => 15],
            ['name' => 'Lazac', 'calories_per_100g' => 208, 'protein_per_100g' => 20, 'carbs_per_100g' => 0, 'fat_per_100g' => 13],
            ['name' => 'Tonhal', 'calories_per_100g' => 132, 'protein_per_100g' => 28, 'carbs_per_100g' => 0, 'fat_per_100g' => 1.3],

            ['name' => 'Tojás', 'calories_per_100g' => 155, 'protein_per_100g' => 13, 'carbs_per_100g' => 1.1, 'fat_per_100g' => 11],
            ['name' => 'Rizs', 'calories_per_100g' => 130, 'protein_per_100g' => 2.7, 'carbs_per_100g' => 28, 'fat_per_100g' => 0.3],
            ['name' => 'Barna rizs', 'calories_per_100g' => 111, 'protein_per_100g' => 2.6, 'carbs_per_100g' => 23, 'fat_per_100g' => 0.9],
            ['name' => 'Tészta', 'calories_per_100g' => 131, 'protein_per_100g' => 5, 'carbs_per_100g' => 25, 'fat_per_100g' => 1.1],
            ['name' => 'Zabpehely', 'calories_per_100g' => 389, 'protein_per_100g' => 17, 'carbs_per_100g' => 66, 'fat_per_100g' => 7],

            ['name' => 'Kenyér', 'calories_per_100g' => 265, 'protein_per_100g' => 9, 'carbs_per_100g' => 49, 'fat_per_100g' => 3.2],
            ['name' => 'Teljes kiőrlésű kenyér', 'calories_per_100g' => 247, 'protein_per_100g' => 13, 'carbs_per_100g' => 41, 'fat_per_100g' => 4.2],
            ['name' => 'Burgonya', 'calories_per_100g' => 77, 'protein_per_100g' => 2, 'carbs_per_100g' => 17, 'fat_per_100g' => 0.1],
            ['name' => 'Édesburgonya', 'calories_per_100g' => 86, 'protein_per_100g' => 1.6, 'carbs_per_100g' => 20, 'fat_per_100g' => 0.1],

            ['name' => 'Tej', 'calories_per_100g' => 42, 'protein_per_100g' => 3.4, 'carbs_per_100g' => 5, 'fat_per_100g' => 1.0],
            ['name' => 'Joghurt', 'calories_per_100g' => 59, 'protein_per_100g' => 10, 'carbs_per_100g' => 3.6, 'fat_per_100g' => 0.4],
            ['name' => 'Túró', 'calories_per_100g' => 98, 'protein_per_100g' => 11, 'carbs_per_100g' => 3.4, 'fat_per_100g' => 4.3],
            ['name' => 'Sajt', 'calories_per_100g' => 402, 'protein_per_100g' => 25, 'carbs_per_100g' => 1.3, 'fat_per_100g' => 33],

            ['name' => 'Mandula', 'calories_per_100g' => 579, 'protein_per_100g' => 21, 'carbs_per_100g' => 22, 'fat_per_100g' => 50],
            ['name' => 'Dió', 'calories_per_100g' => 654, 'protein_per_100g' => 15, 'carbs_per_100g' => 14, 'fat_per_100g' => 65],
            ['name' => 'Mogyoróvaj', 'calories_per_100g' => 588, 'protein_per_100g' => 25, 'carbs_per_100g' => 20, 'fat_per_100g' => 50],

            ['name' => 'Víz', 'calories_per_100g' => 0, 'protein_per_100g' => 0, 'carbs_per_100g' => 0, 'fat_per_100g' => 0],
            ['name' => 'Narancslé', 'calories_per_100g' => 45, 'protein_per_100g' => 0.7, 'carbs_per_100g' => 10.4, 'fat_per_100g' => 0.2],
            ['name' => 'Almalé', 'calories_per_100g' => 46, 'protein_per_100g' => 0.1, 'carbs_per_100g' => 11.3, 'fat_per_100g' => 0.1],
            ['name' => 'Kávé', 'calories_per_100g' => 2, 'protein_per_100g' => 0.1, 'carbs_per_100g' => 0, 'fat_per_100g' => 0],
            ['name' => 'Tea', 'calories_per_100g' => 1, 'protein_per_100g' => 0, 'carbs_per_100g' => 0.2, 'fat_per_100g' => 0],
        ];

        foreach ($foods as $food) {
            Food::updateOrCreate(
                [
                    'name' => $food['name'],
                    'is_custom' => false,
                    'user_id' => null,
                ],
                [
                    'calories_per_100g' => $food['calories_per_100g'],
                    'protein_per_100g' => $food['protein_per_100g'],
                    'carbs_per_100g' => $food['carbs_per_100g'],
                    'fat_per_100g' => $food['fat_per_100g'],
                ]
            );
        }
    }
}

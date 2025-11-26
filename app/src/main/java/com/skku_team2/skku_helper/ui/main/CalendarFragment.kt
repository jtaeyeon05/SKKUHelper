package com.skku_team2.skku_helper.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.databinding.FragmentCalendarBinding


class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO

        // TEST Firebase
        val db = Firebase.firestore

        binding.buttonTest1.setOnClickListener {
            val user = hashMapOf(
                "first" to "Ada",
                "last" to "Lovelace",
                "born" to 1815
            )

            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "DocumentSnapshot added with ID: ${documentReference.id}"
                }
                .addOnFailureListener { exception ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "Error adding document $exception"
                }
        }

        binding.buttonTest2.setOnClickListener {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                                "\n" +
                                "${document.id} => ${document.data}"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "Error getting documents. $exception"
                }
        }

        binding.buttonTest3.setOnClickListener {
            db.collection("test")
                .document("HAHAHA")
                .set(Assignment.default)
                .addOnSuccessListener { documentReference ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "DocumentSnapshot added with ID: $documentReference"
                }
                .addOnFailureListener { exception ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "Error adding document $exception"
                }
        }

        binding.buttonTest4.setOnClickListener {
            db.collection("test")
                .document("HAHAHA")
                .get()
                .addOnSuccessListener { result ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "$result"
                }
                .addOnFailureListener { exception ->
                    binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                            "\n" +
                            "Error getting documents. $exception"
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.gas.presentation.ui.fragment

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gas.databinding.DialogyEmergencyContactBinding
import com.example.gas.databinding.FragmentEmergencyBinding
import com.example.gas.domain.model.EmergencyInfo
import com.example.gas.presentation.viewmodel.EmergencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmergencyViewModel by viewModels()
    private lateinit var adapter: EmergencyContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeContacts()
    }

    private fun setupRecyclerView() {
        adapter = EmergencyContactAdapter(
            onEditClick = { contact ->
                viewModel.prepareEditContact(contact.emergencyId)
                showAddEditDialog()
            },
            onDeleteClick = { contact ->
                showDeleteConfirmationDialog(contact)
            },
            onItemClick = { contact ->
                viewModel.prepareEditContact(contact.emergencyId)
                showAddEditDialog()
            }
        )
        binding.rvEmergencyContacts.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@EmergencyFragment.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            viewModel.resetDialogInputs()
            showAddEditDialog()
        }
    }

    private fun observeContacts() {
        viewModel.contacts.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.ContactsUiState.Loading -> {
                    Timber.tag("EmergencyFragment").d("Loading contacts")
                }
                is EmergencyViewModel.ContactsUiState.Success -> {
                    try {
                        adapter.submitList(state.contacts)
                        Timber.tag("EmergencyFragment")
                            .d("Updated contacts: ${state.contacts.size}")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error updating contacts: ${e.message}", Toast.LENGTH_SHORT).show()
                        Timber.tag("EmergencyFragment")
                            .e(e, "Error updating contacts: ${e.message}")
                    }
                }
                is EmergencyViewModel.ContactsUiState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("EmergencyFragment").e("Error state: ${state.message}")
                }
            }
        }
    }

    private fun showAddEditDialog() {
        val dialogBinding = DialogyEmergencyContactBinding.inflate(LayoutInflater.from(context))
        dialogBinding.viewmodel = viewModel
        dialogBinding.lifecycleOwner = viewLifecycleOwner

        // Setup Relationship Spinner

        // Setup Priority Spinner
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            mutableListOf<Int>()
        ).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
        dialogBinding.spPriority.adapter = priorityAdapter
        viewModel.availablePriorities.observe(viewLifecycleOwner) { priorities ->
            priorityAdapter.clear()
            priorityAdapter.addAll(priorities)
            priorityAdapter.notifyDataSetChanged()
            // Set default selection to the current selected priority or first available
            viewModel.selectedPriority.value?.let { selected ->
                val index = priorities.indexOf(selected)
                if (index >= 0) {
                    dialogBinding.spPriority.setSelection(index)
                }
            }
        }
        dialogBinding.spPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setPriority(priorityAdapter.getItem(position) ?: return)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Setup Dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSave.setOnClickListener {
            viewModel.addOrUpdateContact()
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(contact: EmergencyInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete ${contact.emergencyName}'s contact?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteContact(contact.emergencyId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
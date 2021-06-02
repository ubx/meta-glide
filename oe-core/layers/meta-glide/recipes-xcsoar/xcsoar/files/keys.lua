xcsoar.input_event.new("key_1",
		function(e)
			xcsoar.fire_legacy_event("Setup","Alternates");
		end
)

xcsoar.input_event.new("key_2",
		function(e)
			xcsoar.fire_legacy_event("Setup","Basic");
		end
)
